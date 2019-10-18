package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.rpgquesting.actions.quest.CActionCompleteQuest;
import com.fantasticsource.rpgquesting.actions.quest.CActionStartQuest;
import com.fantasticsource.rpgquesting.compat.Compat;
import com.fantasticsource.rpgquesting.compat.CompatNeat;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.quest.CRelatedDialogueEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_LMAP_COLOR;
import static org.lwjgl.opengl.GL11.*;

public class OverheadIndicators
{
    //Both sides
    public static final int
            FUNC_SET_NONE = 0,
            FUNC_SET_IN_PROGRESS = 1,
            FUNC_SET_AVAILABLE_REPEATABLE = 2,
            FUNC_SET_AVAILABLE = 3,
            FUNC_SET_READY_TO_TURN_IN = 4;

    //Client-side
    private static double SCALE = 1, HOFF2D = 0, VOFF2D = -10, VERTICAL_PERCENT = 1, VERTICAL_OFFSET = 0.5, HORIZONTAL_PERCENT = 0;
    private static boolean ACCOUNT_FOR_SNEAK = true;
    private static final int SIZE = 32;
    private static final double UV_HALF_PIXEL = 0.5 / SIZE;
    public static LinkedHashMap<Integer, Integer> overheadIndicators = new LinkedHashMap<>();

    //Server-side
    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        if (livingBase.world.isRemote || livingBase.getEntityId() % 20 != ServerTickTimer.currentTick() % 20) return;

        for (EntityPlayer player : ((WorldServer) livingBase.world).getEntityTracker().getTrackingPlayers(livingBase))
        {
            update((EntityPlayerMP) player, livingBase);
        }
    }

    //Server-side
    public static void update(EntityPlayerMP player, Entity entity)
    {
        FMLCommonHandler.instance().getMinecraftServerInstance().profiler.startSection("RPG Questing: Update Overhead Indicators");

        CActionStartQuest start = new CActionStartQuest();
        CActionCompleteQuest complete = new CActionCompleteQuest();

        int maxFunc = -1;

        for (CQuest quest : CQuests.QUESTS.worldQuestData.values())
        {
            if (quest.isReadyToComplete(player))
            {
                for (CRelatedDialogueEntry dialogueEntry : quest.relatedDialogues)
                {
                    if (!dialogueEntry.relation.value.equals(complete.relation())) continue;

                    CDialogue dialogue = CDialogues.get(dialogueEntry.dialogueName.value);
                    if (!dialogue.isAvailable(player, entity)) continue;

                    maxFunc = FUNC_SET_READY_TO_TURN_IN;
                    break;
                }

                if (maxFunc == FUNC_SET_READY_TO_TURN_IN) break;
            }
            else if (quest.isAvailable(player))
            {
                if (maxFunc >= FUNC_SET_AVAILABLE || (quest.repeatable.value && maxFunc >= FUNC_SET_AVAILABLE_REPEATABLE)) continue;

                for (CRelatedDialogueEntry dialogueEntry : quest.relatedDialogues)
                {
                    if (!dialogueEntry.relation.value.equals(start.relation())) continue;

                    CDialogue dialogue = CDialogues.get(dialogueEntry.dialogueName.value);
                    if (!dialogue.isAvailable(player, entity)) continue;

                    maxFunc = quest.repeatable.value ? FUNC_SET_AVAILABLE_REPEATABLE : FUNC_SET_AVAILABLE;
                    break;
                }
            }
            else if (quest.isInProgress(player))
            {
                if (maxFunc >= FUNC_SET_IN_PROGRESS) continue;

                for (CRelatedDialogueEntry dialogueEntry : quest.relatedDialogues)
                {
                    if (!dialogueEntry.relation.value.equals(complete.relation())) continue;

                    CDialogue dialogue = CDialogues.get(dialogueEntry.dialogueName.value);
                    if (dialogue.isAvailable(player, entity)) continue;

                    maxFunc = FUNC_SET_IN_PROGRESS;
                    break;
                }
            }
            else
            {
                if (maxFunc >= FUNC_SET_NONE) continue;

                for (CRelatedDialogueEntry dialogueEntry : quest.relatedDialogues)
                {
                    CDialogue dialogue = CDialogues.get(dialogueEntry.dialogueName.value);

                    boolean entityHas = true;
                    for (CCondition condition : dialogue.entityConditions)
                    {
                        if (condition.unmetConditions(entity).size() > 0)
                        {
                            entityHas = false;
                            break;
                        }
                    }

                    if (entityHas)
                    {
                        maxFunc = FUNC_SET_NONE;
                        break;
                    }
                }
            }
        }

        if (maxFunc >= FUNC_SET_NONE) Network.WRAPPER.sendTo(new Network.OverheadIndicatorPacket(entity.getEntityId(), maxFunc), player);

        FMLCommonHandler.instance().getMinecraftServerInstance().profiler.endSection();
    }


    //Client-side
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void entityRender(RenderLivingEvent.Post event)
    {
        if (event.getRenderer().getRenderManager().renderOutlines) return;

        EntityLivingBase livingBase = event.getEntity();
        if (livingBase == null || !overheadIndicators.containsKey(livingBase.getEntityId()) || MCTools.isRidingOrRiddenBy(Minecraft.getMinecraft().player, livingBase)) return;


        ResourceLocation rl;
        switch (overheadIndicators.get(livingBase.getEntityId()))
        {
            case FUNC_SET_READY_TO_TURN_IN:
                rl = new ResourceLocation(RPGQuesting.MODID, "image/ready_to_turn_in.png");
                break;

            case FUNC_SET_AVAILABLE:
                rl = new ResourceLocation(RPGQuesting.MODID, "image/available.png");
                break;

            case FUNC_SET_AVAILABLE_REPEATABLE:
                rl = new ResourceLocation(RPGQuesting.MODID, "image/repeatable_available.png");
                break;

            case FUNC_SET_IN_PROGRESS:
                rl = new ResourceLocation(RPGQuesting.MODID, "image/in_progress.png");
                break;

            default:
                return;
        }

        RenderManager renderManager = event.getRenderer().getRenderManager();
        double x = event.getX(), y = event.getY(), z = event.getZ();

        float viewerYaw = renderManager.playerViewY; //"playerViewY" is LITERALLY the yaw...interpolated over the partialtick
        float viewerPitch = renderManager.playerViewX; //"playerViewX" is LITERALLY the pitch...interpolated over the partialtick

        double scale = SCALE * 0.025;
        double halfSize2D = SIZE / 4D;
        double hOff2D = HOFF2D;
        double vOff2D = Compat.neat ? VOFF2D - 11 : VOFF2D;


        GlStateManager.disableLighting();

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.enableTexture2D();
        Minecraft.getMinecraft().renderEngine.bindTexture(rl);


        GlStateManager.pushMatrix();

        if (Compat.neat)
        {
            GlStateManager.translate(x, y + livingBase.height * VERTICAL_PERCENT + VERTICAL_OFFSET - 0.5 + CompatNeat.heightAboveMob, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(livingBase.width * HORIZONTAL_PERCENT, 0, 0);
            GlStateManager.scale(-scale, -scale, scale);
        }
        else if (Compat.customnpcs && livingBase.getClass().getName().equals("noppes.npcs.entity.EntityCustomNpc"))
        {
            double cnpcScale = livingBase.height / 1.8;
            GlStateManager.translate(x, y + livingBase.height * VERTICAL_PERCENT + VERTICAL_OFFSET - 0.5 - 0.108 * cnpcScale, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(livingBase.width * HORIZONTAL_PERCENT, 0, 0);

            scale *= cnpcScale;
            GlStateManager.scale(-scale, -scale, scale);

            vOff2D -= 45;
        }
        else
        {
            GlStateManager.translate(x, y + livingBase.height * VERTICAL_PERCENT - (ACCOUNT_FOR_SNEAK && livingBase.isSneaking() ? 0.25 : 0) + VERTICAL_OFFSET, z);
            GlStateManager.rotate(-viewerYaw, 0, 1, 0);
            GlStateManager.rotate(renderManager.options.thirdPersonView == 2 ? -viewerPitch : viewerPitch, 1, 0, 0);
            GlStateManager.translate(livingBase.width * HORIZONTAL_PERCENT, 0, 0);
            GlStateManager.scale(-scale, -scale, scale);
        }


        double left = -halfSize2D + hOff2D;
        double right = halfSize2D + hOff2D;
        double top = -halfSize2D + vOff2D;
        double bottom = halfSize2D + vOff2D;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, POSITION_TEX_LMAP_COLOR);

        bufferbuilder.pos(left, top, 0).tex(UV_HALF_PIXEL, 0.5 + UV_HALF_PIXEL).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(left, bottom, 0).tex(UV_HALF_PIXEL, 1 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(right, bottom, 0).tex(0.5 - UV_HALF_PIXEL, 1 - UV_HALF_PIXEL).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(right, top, 0).tex(0.5 - UV_HALF_PIXEL, 0.5 + UV_HALF_PIXEL).lightmap(15728880, 15728880).color(255, 255, 255, 255).endVertex();

        tessellator.draw();


        GlStateManager.popMatrix();

        GlStateManager.disableBlend();

        GlStateManager.enableLighting();
    }
}

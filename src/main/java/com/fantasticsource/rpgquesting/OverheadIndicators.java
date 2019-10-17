package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.rpgquesting.actions.quest.CActionCompleteQuest;
import com.fantasticsource.rpgquesting.actions.quest.CActionStartQuest;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.quest.CRelatedDialogueEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;

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
}

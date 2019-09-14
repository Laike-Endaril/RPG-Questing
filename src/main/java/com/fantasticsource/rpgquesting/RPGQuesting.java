package com.fantasticsource.rpgquesting;

import com.fantasticsource.rpgquesting.actions.CActionBranch;
import com.fantasticsource.rpgquesting.actions.CActionEndDialogue;
import com.fantasticsource.rpgquesting.dialogue.*;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

@Mod(modid = RPGQuesting.MODID, name = RPGQuesting.NAME, version = RPGQuesting.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.021i,)")
public class RPGQuesting
{
    public static final String MODID = "rpgquesting";
    public static final String NAME = "RPG Questing";
    public static final String VERSION = "1.12.2.000";

    public static MinecraftServer server = null;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Network.init();
        MinecraftForge.EVENT_BUS.register(RPGQuesting.class);
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void playerInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event)
    {
        if (event.getSide() == Side.SERVER)
        {
            if (CDialogues.handle((EntityPlayerMP) event.getEntityPlayer(), event.getTarget())) event.setCanceled(true);
        }
        else CDialogues.targetID = event.getTarget().getEntityId();
    }

    @Mod.EventHandler
    public static void serverStart(FMLServerAboutToStartEvent event) throws IOException
    {
        server = event.getServer();
        CQuests.QUESTS.load();
        CDialogues.DIALOGUES.load();


        //TODO test code here
        CDialogueFilter filter = new CDialogueFilter().add(new ResourceLocation("wolf"));

        CDialogueChoice choiceEndDialogue = new CDialogueChoice().setText("End Dialogue").setAction(new CActionEndDialogue());
        CDialogueChoice choiceNo = new CDialogueChoice().setText("No").setAction(new CActionEndDialogue());

        CDialogueBranch branch = new CDialogueBranch("Mornin' @p!  Nice day fer fishin', ain't it?");

        CDialogueBranch branch2 = new CDialogueBranch("Hu-huh!", choiceEndDialogue);
        CDialogueChoice choiceYes = new CDialogueChoice().setText("Yes").setAction(new CActionBranch().set(branch2));

        branch.add(choiceYes, choiceNo);

        CDialogues.add(new CDialogue().setName("Fishin'").add(filter).add(branch).add(branch2));
        CDialogues.add(new CDialogue().setName("The Depths of Waterdeep").add(filter).add(branch).add(branch2));
    }

    @Mod.EventHandler
    public static void serverStop(FMLServerStoppedEvent event) throws IOException
    {
        CQuests.QUESTS.save();
        CDialogues.DIALOGUES.save();
        server = null;
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event) throws IOException
    {
        if (event.player instanceof EntityPlayerMP) CQuests.loadPlayerQuestData((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event) throws IOException
    {
        if (event.player instanceof EntityPlayerMP) CQuests.unloadPlayerQuestData((EntityPlayerMP) event.player);
    }
}

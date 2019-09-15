package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.IOException;

@Mod(modid = RPGQuesting.MODID, name = RPGQuesting.NAME, version = RPGQuesting.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.021i,)")
public class RPGQuesting
{
    public static final String MODID = "rpgquesting";
    public static final String NAME = "RPG Questing";
    public static final String VERSION = "1.12.2.000";

    public static File dataFolder;
    public static boolean addedNewStuff = false;

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
    public static void serverStart(FMLServerStartingEvent event) throws IOException
    {
        dataFolder = new File(MCTools.getWorldSaveDir(event.getServer()) + MODID + File.separator);
        CQuests.QUESTS.load();
        CDialogues.DIALOGUES.load();


        //TODO test code here
        if (!addedNewStuff)
        {
            addedNewStuff = true;

//            CDialogue dialogue = new CDialogue().setName("The Depths of Waterdeep");
//            CDialogues.add(dialogue);
//
//            dialogue.add(new CConditionEntityEntryIs("wolf"), new CConditionNameIs("chicken"));
//
//            CDialogueBranch branch = new CDialogueBranch("Hey @p, you wanna know a secret?");
//            CDialogueBranch branch2 = new CDialogueBranch("Well too bad.");
//            dialogue.add(branch, branch2);
//
//            branch.add(new CDialogueChoice().setText("Yes").setAction(new CActionBranch().set(branch2)));
//            branch.add(new CDialogueChoice().setText("No").setAction(new CActionEndDialogue()));
//
//            branch2.add(new CDialogueChoice().setText("End Dialogue").setAction(new CActionEndDialogue()));
        }
    }

    @Mod.EventHandler
    public static void serverStop(FMLServerStoppedEvent event) throws IOException
    {
        CDialogues.DIALOGUES.save().clear();
        CQuests.QUESTS.save().clear();
        dataFolder = null;
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

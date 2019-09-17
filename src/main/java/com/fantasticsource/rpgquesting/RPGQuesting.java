package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.quest.JournalGUI;
import com.fantasticsource.rpgquesting.quest.QuestTracker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.IOException;

@Mod(modid = RPGQuesting.MODID, name = RPGQuesting.NAME, version = RPGQuesting.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.021i,)")
public class RPGQuesting
{
    public static final String MODID = "rpgquesting";
    public static final String NAME = "RPG Questing";
    public static final String VERSION = "1.12.2.000";

    public static File worldDataFolder, playerDataFolder;
    public static boolean addedNewStuff = false;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Network.init();
        MinecraftForge.EVENT_BUS.register(RPGQuesting.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            Keys.init(event);
        }
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void playerInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event)
    {
        if (event.getSide() == Side.SERVER && event.getHand() == EnumHand.MAIN_HAND)
        {
            if (CDialogues.handle((EntityPlayerMP) event.getEntityPlayer(), event.getTarget())) event.setCanceled(true);
        }
        else CDialogues.targetID = event.getTarget().getEntityId();
    }

    @Mod.EventHandler
    public static void serverStart(FMLServerStartingEvent event) throws IOException
    {
        worldDataFolder = new File(MCTools.getWorldSaveDir(event.getServer()) + MODID + File.separator);
        playerDataFolder = new File(MCTools.getPlayerDataDir(event.getServer()));
        CQuests.QUESTS.load();
        CDialogues.DIALOGUES.load();


        //TODO test code here
        if (!addedNewStuff)
        {
            addedNewStuff = true;

//            CQuest quest = new CQuest("The Wolf named Chicken", "The Wolves", 1, false);
//            CQuests.add(quest);
//
//            quest.add(new CObjectiveKill("chickens killed", 5, new CConditionEntityEntryIs("chicken")));
//            quest.add(new ItemStack(Items.CHICKEN));
//            quest.setExp(5);
//
//
//            CDialogue dialogue = new CDialogue().setName("The Wolf named Chicken");
//            CDialogues.add(dialogue);
//
//            dialogue.addPlayerConditions(new CConditionQuestAvailable(quest));
//            dialogue.addEntityConditions(new CConditionEntityEntryIs("wolf"), new CConditionNameIs("Chicken"));
//
//            CDialogueBranch branch = new CDialogueBranch("Yeah, that's right...despite being a wolf, my name is \"Chicken\".  Freaking...you know what?  Go kill 5 chickens for me and maybe I'll tell you how I got the name");
//            dialogue.add(branch);
//            branch.add(new CDialogueChoice("Alright").setAction(new CActionArray(new CActionStartQuest(quest), new CActionEndDialogue())));
//            branch.add(new CDialogueChoice("Nah").setAction(new CActionEndDialogue()));
//
//
//            dialogue = new CDialogue().setName("The Wolf named Chicken (in progress)");
//            CDialogues.add(dialogue);
//
//            dialogue.addPlayerConditions(new CConditionQuestInProgress(quest));
//            dialogue.addEntityConditions(new CConditionEntityEntryIs("wolf"), new CConditionNameIs("Chicken"));
//
//            branch = new CDialogueBranch("You kill those chickens yet?  Doesn't look like it...");
//            dialogue.add(branch);
//            branch.add(new CDialogueChoice("End Dialogue").setAction(new CActionEndDialogue()));
//
//
//            dialogue = new CDialogue().setName("The Wolf named Chicken (complete)");
//            CDialogues.add(dialogue);
//
//            dialogue.addPlayerConditions(new CConditionQuestReadyToComplete(quest));
//            dialogue.addEntityConditions(new CConditionEntityEntryIs("wolf"), new CConditionNameIs("Chicken"));
//
//            branch = new CDialogueBranch("Hey, grats.  You killed some helpless chickens.  Slow clap.");
//            CDialogueBranch branch2 = new CDialogueBranch("...None of your business.  Now take this corpse and scram.");
//            dialogue.add(branch, branch2);
//
//            CDialogueChoice choice = new CDialogueChoice("Right...so how did you get your name?");
//            branch.add(choice);
//            choice.setAction(new CActionArray(new CActionBranch(branch2), new CActionCompleteQuest(quest)).addConditions(new CConditionInventorySpace(1)));
//            branch.add(new CDialogueChoice("End Dialogue").setAction(new CActionEndDialogue()));
//
//            branch2.add(new CDialogueChoice("Ugh").setAction(new CActionEndDialogue()));
        }
    }

    @Mod.EventHandler
    public static void serverStop(FMLServerStoppedEvent event) throws IOException
    {
        CDialogues.DIALOGUES.save().clear();
        CQuests.QUESTS.save().clear();
        worldDataFolder = null;
        playerDataFolder = null;
    }

    @SubscribeEvent
    public static void serverLogin(PlayerEvent.PlayerLoggedInEvent event) throws IOException
    {
        if (event.player instanceof EntityPlayerMP) CQuests.loadPlayerQuestData((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public static void serverLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP) CQuests.unloadPlayerQuestData((EntityPlayerMP) event.player);
    }

    @Mod.EventHandler
    public static void clientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        JournalGUI.clear();
        QuestTracker.stopTracking();
    }
}

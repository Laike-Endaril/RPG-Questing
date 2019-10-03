package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.gui.JournalGUI;
import com.fantasticsource.rpgquesting.quest.CQuests;
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

@Mod(modid = RPGQuesting.MODID, name = RPGQuesting.NAME, version = RPGQuesting.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.022,)")
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
            MinecraftForge.EVENT_BUS.register(QuestTracker.class);
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
            Temp.addKillQuest(false);
            Temp.addCollectionQuest(true);
            Temp.addCollectionQuest2(false);
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
    public static void serverLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                try
                {
                    CQuests.loadPlayerQuestData((EntityPlayerMP) event.player);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
        }
    }

    @SubscribeEvent
    public static void serverLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP) CQuests.unloadPlayerQuestData((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public static void clientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        JournalGUI.clear();
        QuestTracker.stopTracking();
    }
}

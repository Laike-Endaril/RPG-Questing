package com.fantasticsource.rpgquesting;

import com.fantasticsource.rpgquesting.dialogue.Dialogues;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = RPGQuesting.MODID, name = RPGQuesting.NAME, version = RPGQuesting.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.021i,)")
public class RPGQuesting
{
    public static final String MODID = "rpgquesting";
    public static final String NAME = "RPG Questing";
    public static final String VERSION = "1.12.2.000";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
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
            if (Dialogues.handle((EntityPlayerMP) event.getEntityPlayer(), event.getTarget())) event.setCanceled(true);
        }
    }
}

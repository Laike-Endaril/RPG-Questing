package com.fantasticsource.rpgquesting;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;

public class OverheadIndicators
{
    //Client-side
    public static LinkedHashMap<Integer, Integer> overheadIndicators = new LinkedHashMap<>();


    //Server-side
    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event)
    {
        update((EntityPlayerMP) event.getEntityPlayer(), event.getEntity());
    }

    //Server-side
    @SubscribeEvent
    public static void stopTracking(PlayerEvent.StopTracking event)
    {
        remove((EntityPlayerMP) event.getEntityPlayer(), event.getEntity());
    }

    //Server-side
    public static void updatePlayersTracking(Entity entity)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        for (WorldServer world : server.worlds)
        {
            for (EntityPlayer player : world.getEntityTracker().getTrackingPlayers(entity))
            {
                update((EntityPlayerMP) player, entity);
            }
        }
    }

    //Server-side
    public static void update(EntityPlayerMP player, Entity entity)
    {

    }

    //Server-side
    public static void remove(EntityPlayerMP player, Entity entity)
    {

    }
}

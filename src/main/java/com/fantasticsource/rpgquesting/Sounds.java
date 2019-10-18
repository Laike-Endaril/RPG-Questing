package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.sound.SimpleClientSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sounds
{
    public static SimpleClientSound OBJECTIVE_COMPLETE;

    @SubscribeEvent
    public static void itemRegistry(RegistryEvent.Register<SoundEvent> event)
    {
        OBJECTIVE_COMPLETE = new SimpleClientSound(event.getRegistry(), new ResourceLocation(RPGQuesting.MODID, "objective_complete"), SoundCategory.MASTER);
    }
}

package com.fantasticsource.rpgquesting;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class Sounds
{
    public static final ResourceLocation
            OBJECTIVE_COMPLETE = new ResourceLocation(RPGQuesting.MODID, "objective_complete"),
            QUEST_ACCEPTED = new ResourceLocation(RPGQuesting.MODID, "quest_accepted"),
            QUEST_COMPLETE = new ResourceLocation(RPGQuesting.MODID, "quest_complete");

    @SubscribeEvent
    public static void soundEventRegistry(RegistryEvent.Register<SoundEvent> event)
    {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();
        registry.register(new SoundEvent(OBJECTIVE_COMPLETE).setRegistryName(OBJECTIVE_COMPLETE));
        registry.register(new SoundEvent(QUEST_ACCEPTED).setRegistryName(QUEST_ACCEPTED));
        registry.register(new SoundEvent(QUEST_COMPLETE).setRegistryName(QUEST_COMPLETE));
    }
}

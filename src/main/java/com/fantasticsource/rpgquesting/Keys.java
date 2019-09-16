package com.fantasticsource.rpgquesting;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final KeyBinding JOURNAL_KEY = new KeyBinding(RPGQuesting.MODID + ".key.journal", KeyConflictContext.UNIVERSAL, Keyboard.KEY_J, RPGQuesting.MODID + ".keyCategory");

    public static void init(FMLPreInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(JOURNAL_KEY);
    }

    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent event)
    {
        if (JOURNAL_KEY.isPressed() && JOURNAL_KEY.getKeyConflictContext().isActive())
        {
            //TODO
        }
    }
}

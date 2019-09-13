package com.fantasticsource.rpgquesting;

import com.fantasticsource.rpgquesting.dialogue.*;
import com.fantasticsource.rpgquesting.dialogue.actions.CActionEndDialogue;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
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
        Network.init();
        MinecraftForge.EVENT_BUS.register(RPGQuesting.class);

        //TODO test code here
        CDialogueFilter filter = new CDialogueFilter().add(new ResourceLocation("wolf"));

        CDialogueChoice choiceEndDialogue = new CDialogueChoice().setText("End Dialogue").add(new CActionEndDialogue());
        CDialogueBranch branch = new CDialogueBranch("Stuff happens", choiceEndDialogue);

        Dialogues.add(new CDialogue("Wirts_Arm", "Wirt's Arm").add(filter).add(branch));
        Dialogues.add(new CDialogue("The_Depths_of_Waterdeep", "The Depths of Waterdeep").add(filter).add(branch));
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
        else Dialogues.targetID = event.getTarget().getEntityId();
    }
}

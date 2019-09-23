package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.Network;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class QuestEditorGUI extends GUIScreen
{
    public static QuestEditorGUI GUI;

    static
    {
        GUI = new QuestEditorGUI();
        MinecraftForge.EVENT_BUS.register(QuestEditorGUI.class);
    }

    public static void show(CQuest questToEdit)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);
    }

    @Override
    protected void init()
    {

    }

    @Override
    public void onGuiClosed()
    {
        Network.WRAPPER.sendToServer(new Network.RequestJournalDataPacket());
    }
}

package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.gui.GUIScreen;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class DialogueGUI extends GUIScreen
{
    public static DialogueGUI GUI;

    static
    {
        GUI = new DialogueGUI();
    }

    public static ArrayList<Dialogue> dialogues = new ArrayList<>();

    private DialogueGUI()
    {
    }

    @Override
    protected void init()
    {

    }

    @Override
    public void show()
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);
    }
}

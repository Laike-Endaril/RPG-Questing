package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class DialogueGUI extends GUIScreen
{
    public static DialogueGUI GUI;

    static
    {
        GUI = new DialogueGUI();
    }

    private DialogueGUI()
    {
    }

    public static void show(CDialogue dialogue)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);
    }

    @Override
    protected void init()
    {
        GUIRectScrollView scrollView = new GUIRectScrollView(this, 0, 0, 0.98, 1);
        guiElements.add(scrollView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
    }
}

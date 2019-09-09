package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class DialoguesGUI extends GUIScreen
{
    public static DialoguesGUI GUI;

    static
    {
        GUI = new DialoguesGUI();
    }

    private DialoguesGUI()
    {
    }

    public static void show(ArrayList<Dialogue> dialogues)
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

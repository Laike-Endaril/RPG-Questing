package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectScrollView;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectTabView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class JournalGUI extends GUIScreen
{
    public static JournalGUI GUI;

    static
    {
        GUI = new JournalGUI();
    }

    private JournalGUI()
    {
    }

    public static void show()
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);
    }

    @Override
    protected void init()
    {
        guiElements.add(new GUIRectTabView(this, 0, 0, 0.5, 1, "In Progress", "Available", "Completed"));
        GUIRectScrollView scrollView = new GUIRectScrollView(this, 0.5, 0, 0.48, 1);
        guiElements.add(scrollView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
    }
}

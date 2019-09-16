package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Network.JournalPacket;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class JournalGUI extends GUIScreen
{
    public static JournalGUI GUI;
    private static GUITabView navigator;

    static
    {
        GUI = new JournalGUI();
    }

    private JournalGUI()
    {
    }

    public static void show(JournalPacket packet)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);

        CPlayerQuestData data = packet.data;
        //TODO
    }

    @Override
    protected void init()
    {
        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        navigator = new GUITabView(this, 0, 0, 0.5, 1, "In Progress", "Completed");
        guiElements.add(navigator);


        //In progress quests
        GUIScrollView scrollView = new GUIScrollView(this, 0.96, 1);
        navigator.tabViews[0].add(scrollView);
        navigator.tabViews[0].add(new GUIVerticalScrollbar(this, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));


        //Completed quests
        scrollView = new GUIScrollView(this, 0.96, 1);
        navigator.tabViews[1].add(scrollView);
        navigator.tabViews[1].add(new GUIVerticalScrollbar(this, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));


        scrollView = new GUIScrollView(this, 0.5, 0, 0.48, 1);
        guiElements.add(scrollView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
    }
}

package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Network.ObfJournalPacket;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class JournalGUI extends GUIScreen
{
    public static JournalGUI GUI;
    private static GUITabView navigator;
    private static GUIScrollView inProgress, completed, questData;

    static
    {
        GUI = new JournalGUI();
    }

    private JournalGUI()
    {
    }

    public static void show(ObfJournalPacket packet)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);

        CPlayerQuestData data = packet.data;

        inProgress.clear();

        completed.clear();

        questData.clear();
    }

    @Override
    protected void init()
    {
        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        navigator = new GUITabView(this, 0, 0, 0.5, 1, "In Progress", "Completed");
        guiElements.add(navigator);


        inProgress = new GUIScrollView(this, 0.96, 1);
        navigator.tabViews[0].add(inProgress);
        navigator.tabViews[0].add(new GUIVerticalScrollbar(this, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, inProgress));


        completed = new GUIScrollView(this, 0.96, 1);
        navigator.tabViews[1].add(completed);
        navigator.tabViews[1].add(new GUIVerticalScrollbar(this, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, completed));


        questData = new GUIScrollView(this, 0.5, 0, 0.48, 1);
        guiElements.add(questData);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, questData));
    }
}

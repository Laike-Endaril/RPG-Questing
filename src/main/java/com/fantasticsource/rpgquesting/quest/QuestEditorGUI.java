package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class QuestEditorGUI extends GUIScreen
{
    public static QuestEditorGUI GUI;

    private static GUITabView tabView;
    private static GUIScrollView main, objectives, rewards, conditions;

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
        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        tabView = new GUITabView(this, 1, 1, "Main", "Objectives", "Rewards", "Availability Conditions");
        guiElements.add(tabView);

        main = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(0).add(main);
        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, main));

        objectives = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(1).add(objectives);
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectives));

        rewards = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(2).add(rewards);
        tabView.tabViews.get(2).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, rewards));

        conditions = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(3).add(conditions);
        tabView.tabViews.get(3).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditions));
    }

    @Override
    public void onGuiClosed()
    {
        Network.WRAPPER.sendToServer(new Network.RequestJournalDataPacket());
    }
}

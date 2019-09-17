package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Network.ObfJournalPacket;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Map;

public class JournalGUI extends GUIScreen
{
    public static JournalGUI GUI;
    private static GUITabView navigator;
    private static GUIScrollView inProgress, completed, questView;

    public static String currentQuestname = "";
    public static ArrayList<CObjective> currentObjectives = new ArrayList<>();

    static
    {
        GUI = new JournalGUI();
    }

    private JournalGUI()
    {
    }

    public static void stopTrackingCurrent()
    {
        currentQuestname = "";
        currentObjectives.clear();
    }

    public static void show(ObfJournalPacket packet)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);

        CPlayerQuestData data = packet.data;

        inProgress.clear();
        for (Map.Entry<String, ArrayList<CObjective>> entry : data.inProgressQuests.entrySet())
        {
            boolean done = true, started = false;
            for (CObjective objective : entry.getValue())
            {
                if (!objective.isDone()) done = false;
                if (objective.isStarted()) started = true;
                if (started && !done) break;
            }

            Color c = done ? Color.GREEN : started ? Color.YELLOW : Color.RED;
            inProgress.add(new GUIText(GUI, entry.getKey() + "\n", c.copy().setVF(0.5f * c.vf()), c, Color.WHITE));
        }

        completed.clear();
        for (String s : data.completedQuests)
        {
            completed.add(new GUIText(GUI, s + "\n", Color.BLUE));
        }

        String selectedQuestName = packet.selectedQuest.value;
        if (selectedQuestName != null && !selectedQuestName.equals(""))
        {
            if (data.completedQuests.contains(selectedQuestName))
            {
                questView.clear();
                questView.add(new GUIText(GUI, selectedQuestName + "\n\n* QUEST COMPLETED *", Color.BLUE));
            }
            else
            {
                ArrayList<CObjective> objectives = data.inProgressQuests.get(selectedQuestName);
                if (objectives != null)
                {
                    questView.clear();

                    currentQuestname = selectedQuestName;
                    currentObjectives = objectives;

                    boolean done = true, started = false;
                    for (CObjective objective : objectives)
                    {
                        if (!objective.isDone()) done = false;
                        if (objective.isStarted()) started = true;
                        if (started && !done) break;
                    }

                    questView.add(new GUIText(GUI, selectedQuestName + "\n\n", done ? Color.GREEN : started ? Color.YELLOW : Color.RED));
                    for (CObjective objective : objectives)
                    {
                        questView.add(new GUIText(GUI, "* " + objective.getFullText() + "\n", objective.isDone() ? Color.GREEN : objective.isStarted() ? Color.YELLOW : Color.RED));
                    }
                }
            }
        }
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


        questView = new GUIScrollView(this, 0.5, 0, 0.48, 1);
        guiElements.add(questView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, questView));
    }
}

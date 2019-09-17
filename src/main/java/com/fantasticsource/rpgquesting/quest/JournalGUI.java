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
import java.util.LinkedHashMap;
import java.util.Map;

public class JournalGUI extends GUIScreen
{
    private static final Color
            RED = Color.RED.copy().setVF(0.5f),
            YELLOW = Color.YELLOW.copy().setVF(0.5f),
            GREEN = Color.GREEN.copy().setVF(0.5f),
            BLUE = Color.BLUE.copy().setVF(0.5f);

    public static JournalGUI GUI;
    private static GUITabView navigator;
    private static GUIScrollView inProgress, completed, questView;

    public static boolean editable = false;
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

        editable = false;

        CPlayerQuestData data = packet.data;

        LinkedHashMap<String, Boolean> knownQuestGroupCompletion = new LinkedHashMap<>();


        //Quests in progress
        inProgress.clear();
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<CObjective>>> entry2 : data.inProgressQuests.entrySet())
        {
            boolean groupDone = true;

            GUIText groupText = new GUIText(GUI, entry2.getKey().toUpperCase() + "\n");
            inProgress.add(groupText);

            for (Map.Entry<String, ArrayList<CObjective>> entry : entry2.getValue().entrySet())
            {
                boolean done = true, started = false;
                for (CObjective objective : entry.getValue())
                {
                    if (!objective.isDone()) done = false;
                    if (objective.isStarted()) started = true;
                    if (started && !done) break;
                }
                if (!done) groupDone = false;

                Color c = done ? GREEN : started ? YELLOW : RED;
                inProgress.add(new GUIText(GUI, "* " + entry.getKey() + "\n", c, c.copy().setVF(0.75f), Color.WHITE));
            }

            knownQuestGroupCompletion.put(entry2.getKey(), groupDone);

            groupText.setColor(groupDone ? GREEN : YELLOW);
            inProgress.add(new GUIText(GUI, "\n"));
        }
        if (inProgress.size() > 0) inProgress.remove(inProgress.size() - 1);


        //Complated quests
        completed.clear();
        for (Map.Entry<String, ArrayList<String>> entry : data.completedQuests.entrySet())
        {
            Boolean groupDone = knownQuestGroupCompletion.get(entry.getKey());
            Color c = (groupDone == null || groupDone) ? GREEN : YELLOW;

            completed.add(new GUIText(GUI, entry.getKey().toUpperCase() + "\n", c, c.copy().setVF(0.75f), Color.WHITE));
            for (String s : entry.getValue())
            {
                completed.add(new GUIText(GUI, "* " + s + "\n", BLUE));
            }
            inProgress.add(new GUIText(GUI, "\n"));
        }
        if (completed.size() > 0) completed.remove(completed.size() - 1);


        //Currently selected quest
        String selectedQuestName = packet.selectedQuest.value;
        if (selectedQuestName != null && !selectedQuestName.equals("")) currentQuestname = selectedQuestName;


        if (currentQuestname != null && !currentQuestname.equals(""))
        {
            boolean questDone = false;
            for (Map.Entry<String, ArrayList<String>> entry : data.completedQuests.entrySet())
            {
                if (entry.getValue().contains(currentQuestname))
                {
                    questView.clear();
                    questView.add(new GUIText(GUI, currentQuestname.toUpperCase() + "\n\n* Quest Completed! *", BLUE));
                    questDone = true;
                    break;
                }
            }

            if (!questDone)
            {
                for (Map.Entry<String, LinkedHashMap<String, ArrayList<CObjective>>> entry : data.inProgressQuests.entrySet())
                {
                    ArrayList<CObjective> objectives = entry.getValue().get(currentQuestname);
                    if (objectives != null)
                    {
                        currentObjectives = objectives;

                        questView.clear();

                        boolean done = true, started = false;
                        for (CObjective objective : currentObjectives)
                        {
                            if (!objective.isDone()) done = false;
                            if (objective.isStarted()) started = true;
                            if (started && !done) break;
                        }

                        questView.add(new GUIText(GUI, currentQuestname.toUpperCase() + "\n\n", done ? GREEN : started ? YELLOW : RED));
                        for (CObjective objective : currentObjectives)
                        {
                            questView.add(new GUIText(GUI, "* " + objective.getFullText() + "\n", objective.isDone() ? GREEN : objective.isStarted() ? YELLOW : RED));
                        }

                        break;
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

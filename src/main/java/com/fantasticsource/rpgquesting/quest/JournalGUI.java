package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextSpoiler;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Network.ObfJournalPacket;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class JournalGUI extends GUIScreen
{
    public static final Color
            RED = Color.RED.copy().setVF(0.5f),
            YELLOW = Color.YELLOW.copy().setVF(0.5f),
            GREEN = Color.GREEN.copy().setVF(0.5f),
            BLUE = Color.BLUE.copy().setVF(0.5f);

    public static JournalGUI GUI;
    public static boolean editable = false;
    public static CPlayerQuestData data;
    private static GUITabView navigator;
    private static GUIScrollView inProgress, completed, questView;

    private static LinkedHashMap<GUIText, String> inProgressQuestToString = new LinkedHashMap<>();
    private static LinkedHashMap<GUIText, String> completedQuestToString = new LinkedHashMap<>();
    private static LinkedHashMap<String, GUIText> inProgressStringToQuest = new LinkedHashMap<>();
    private static LinkedHashMap<String, GUIText> completedStringToQuest = new LinkedHashMap<>();

    private static LinkedHashMap<GUITextSpoiler, String> inProgressGroupToString = new LinkedHashMap<>();
    private static LinkedHashMap<GUITextSpoiler, String> completedGroupToString = new LinkedHashMap<>();

    private static String viewedQuest = null;

    static
    {
        GUI = new JournalGUI();
        MinecraftForge.EVENT_BUS.register(JournalGUI.class);
    }

    private JournalGUI()
    {
    }

    public static void show(ObfJournalPacket packet)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);

        editable = false;

        data = packet.data;

        LinkedHashMap<String, Boolean> knownQuestGroupCompletion = new LinkedHashMap<>();


        //Quests in progress
        inProgress.clear();
        inProgressGroupToString.clear();
        inProgressQuestToString.clear();
        inProgressStringToQuest.clear();
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<CObjective>>> entry2 : data.inProgressQuests.entrySet())
        {
            boolean groupDone = true;

            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, entry2.getKey().toUpperCase() + "\n");
            inProgressGroupToString.put(groupSpoiler, entry2.getKey());
            inProgress.add(groupSpoiler);

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
                GUIText quest = new GUIText(GUI, "* " + entry.getKey() + "\n", c, c.copy().setVF(0.75f), Color.WHITE);
                groupSpoiler.add(quest);
                inProgressQuestToString.put(quest, entry.getKey());
                inProgressStringToQuest.put(entry.getKey(), quest);
            }

            knownQuestGroupCompletion.put(entry2.getKey(), groupDone);

            Color c = groupDone ? GREEN : YELLOW;
            groupSpoiler.setColor(c, c.copy().setVF(0.75f), Color.WHITE);
            inProgress.add(new GUIText(GUI, "\n"));
        }
        if (inProgress.size() > 0) inProgress.remove(inProgress.size() - 1);


        //Completed quests
        completed.clear();
        completedGroupToString.clear();
        completedQuestToString.clear();
        completedStringToQuest.clear();
        for (Map.Entry<String, ArrayList<String>> entry : data.completedQuests.entrySet())
        {
            Boolean groupDone = knownQuestGroupCompletion.get(entry.getKey());
            Color c = (groupDone == null || groupDone) ? GREEN : YELLOW;

            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, entry.getKey().toUpperCase() + "\n", c, c.copy().setVF(0.75f), Color.WHITE);
            completedGroupToString.put(groupSpoiler, entry.getKey());
            completed.add(groupSpoiler);

            for (String s : entry.getValue())
            {
                GUIText quest = new GUIText(GUI, "* " + s + "\n", BLUE);
                groupSpoiler.add(quest);
                completedQuestToString.put(quest, s);
                completedStringToQuest.put(s, quest);
            }
            completed.add(new GUIText(GUI, "\n"));
        }
        if (completed.size() > 0) completed.remove(completed.size() - 1);


        //Currently selected quest
        setViewedQuest(packet.selectedQuest.value);
    }

    public static void clear()
    {
        editable = false;
        data = null;
        viewedQuest = null;


        if (GUI == null || !GUI.isInitialized()) return;


        inProgressGroupToString.clear();
        completedGroupToString.clear();

        inProgressQuestToString.clear();
        completedQuestToString.clear();
        inProgressStringToQuest.clear();
        completedStringToQuest.clear();

        inProgress.clear();
        completed.clear();
        questView.clear();
    }

    public static void setViewedQuest(String questName)
    {
        if (questName != null && !questName.equals("")) viewedQuest = questName;
        updateQuestView();
    }

    public static void updateQuestView()
    {
        if (data == null) return;


        //Search in-progress quests
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<CObjective>>> entry : data.inProgressQuests.entrySet())
        {
            ArrayList<CObjective> objectives = entry.getValue().get(viewedQuest);
            if (objectives != null)
            {
                questView.clear();

                boolean done = true, started = false;
                for (CObjective objective : objectives)
                {
                    if (!objective.isDone()) done = false;
                    if (objective.isStarted()) started = true;
                    if (started && !done) break;
                }

                Color c = done ? GREEN : started ? YELLOW : RED;
                questView.add(new GUIText(GUI, viewedQuest.toUpperCase() + "\n\n", c, c.copy().setVF(0.75f), Color.WHITE));

                for (CObjective objective : objectives)
                {
                    questView.add(new GUIText(GUI, "* " + objective.getFullText() + "\n", objective.isDone() ? GREEN : objective.isStarted() ? YELLOW : RED));
                }


                navigator.setActiveTab(0);

                GUIText questElement = inProgressStringToQuest.get(viewedQuest);
                GUITextSpoiler selectedGroup = null;
                for (GUITextSpoiler group : inProgressGroupToString.keySet())
                {
                    if (group.indexOf(questElement) != -1)
                    {
                        selectedGroup = group;
                        group.show();
                    }
                    else group.hide();
                }

                inProgress.focus(selectedGroup);

                return;
            }
        }


        //Search completed quests
        for (Map.Entry<String, ArrayList<String>> entry : data.completedQuests.entrySet())
        {
            if (entry.getValue().contains(viewedQuest))
            {
                questView.clear();

                Color c = BLUE;
                questView.add(new GUIText(GUI, viewedQuest.toUpperCase() + "\n\n", c, c.copy().setVF(0.75f), Color.WHITE));

                questView.add(new GUIText(GUI, "* Quest Completed! *", BLUE));


                navigator.setActiveTab(1);

                GUIText questElement = completedStringToQuest.get(viewedQuest);
                GUITextSpoiler selectedGroup = null;
                for (GUITextSpoiler group : completedGroupToString.keySet())
                {
                    if (group.indexOf(questElement) != -1)
                    {
                        selectedGroup = group;
                        group.show();
                    }
                    else group.hide();
                }

                completed.focus(selectedGroup);

                return;
            }
        }
    }

    @SubscribeEvent
    public static void click(GUILeftClickEvent event)
    {
        if (event.getScreen() != GUI) return;

        GUIElement element = event.getElement();
        if (element.getClass() != GUIText.class) return;


        int index = questView.indexOf(element);
        if (index != -1)
        {
            if (index == 0) setViewedQuest(viewedQuest);
        }


        String questName = inProgressQuestToString.get(element);
        if (questName != null)
        {
            setViewedQuest(questName);
            return;
        }

        questName = completedQuestToString.get(element);
        if (questName != null)
        {
            setViewedQuest(questName);
            return;
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

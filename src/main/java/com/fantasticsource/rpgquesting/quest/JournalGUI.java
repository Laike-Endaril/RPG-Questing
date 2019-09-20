package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpoiler;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Network;
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
    public static final Color[]
            RED = new Color[]{Color.RED.copy().setVF(0.5f), Color.RED.copy().setVF(0.75f), Color.WHITE},
            YELLOW = new Color[]{Color.YELLOW.copy().setVF(0.5f), Color.YELLOW.copy().setVF(0.75f), Color.WHITE},
            GREEN = new Color[]{Color.GREEN.copy().setVF(0.5f), Color.GREEN.copy().setVF(0.75f), Color.WHITE},
            BLUE = new Color[]{Color.BLUE.copy().setVF(0.5f), Color.BLUE.copy().setVF(0.75f), Color.WHITE};

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

    public static void show(CPlayerQuestData dataIn, String trackedQuest)
    {
        if (dataIn != null) data = dataIn;
        if (data == null) return;

        Minecraft.getMinecraft().displayGuiScreen(GUI);

        editable = false;

        LinkedHashMap<String, Boolean> knownQuestGroupCompletion = new LinkedHashMap<>();


        //Quests in progress
        inProgress.clear();
        inProgressGroupToString.clear();
        inProgressQuestToString.clear();
        inProgressStringToQuest.clear();

        inProgress.add(new GUIText(GUI, "\n"));
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<CObjective>>> entry2 : data.inProgressQuests.entrySet())
        {
            boolean groupDone = true;

            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, " " + entry2.getKey());
            inProgressGroupToString.put(groupSpoiler, entry2.getKey());
            inProgress.add(groupSpoiler);

            for (Map.Entry<String, ArrayList<CObjective>> entry : entry2.getValue().entrySet())
            {
                groupSpoiler.add(new GUIText(GUI, "\n"));

                boolean done = true, started = false;
                for (CObjective objective : entry.getValue())
                {
                    if (!objective.isDone()) done = false;
                    if (objective.isStarted()) started = true;
                    if (started && !done) break;
                }
                if (!done) groupDone = false;

                Color[] c = done ? GREEN : started ? YELLOW : RED;
                GUIText quest = new GUIText(GUI, " * " + entry.getKey() + "\n", c[0], c[1], c[2]);
                groupSpoiler.add(quest);
                inProgressQuestToString.put(quest, entry.getKey());
                inProgressStringToQuest.put(entry.getKey(), quest);
            }

            knownQuestGroupCompletion.put(entry2.getKey(), groupDone);

            Color[] c = groupDone ? GREEN : YELLOW;
            groupSpoiler.setColor(c[0], c[1], c[2]);

            groupSpoiler.add(0, new GUIText(GUI, "\n==============================================================================================", c[0]));
            groupSpoiler.add(new GUIText(GUI, "\n==============================================================================================\n", c[0]));

            inProgress.add(new GUIText(GUI, "\n"));
        }
        if (inProgress.size() > 0) inProgress.remove(inProgress.size() - 1);


        //Completed quests
        completed.clear();
        completedGroupToString.clear();
        completedQuestToString.clear();
        completedStringToQuest.clear();

        completed.add(new GUIText(GUI, "\n"));
        for (Map.Entry<String, ArrayList<String>> entry : data.completedQuests.entrySet())
        {
            Boolean groupDone = knownQuestGroupCompletion.get(entry.getKey());
            Color[] c = groupDone == null ? BLUE : groupDone ? GREEN : YELLOW;

            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, " " + entry.getKey(), c[0], c[1], c[2]);
            completedGroupToString.put(groupSpoiler, entry.getKey());
            completed.add(groupSpoiler);

            for (String s : entry.getValue())
            {
                groupSpoiler.add(new GUIText(GUI, "\n"));

                Color[] c1 = inProgressStringToQuest.containsKey(s) ? GREEN : BLUE;
                GUIText quest = new GUIText(GUI, " * " + s + "\n", c1[0], c1[1], c1[2]);
                groupSpoiler.add(quest);
                completedQuestToString.put(quest, s);
                completedStringToQuest.put(s, quest);
            }

            groupSpoiler.add(0, new GUIText(GUI, "\n==============================================================================================", c[0]));
            groupSpoiler.add(new GUIText(GUI, "\n==============================================================================================\n", c[0]));

            completed.add(new GUIText(GUI, "\n"));
        }
        if (completed.size() > 0) completed.remove(completed.size() - 1);


        //Currently selected quest
        setViewedQuest(trackedQuest);
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
        if (questName == null || questName.equals("")) return;
        viewedQuest = questName;

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

                Color[] c = done ? GREEN : started ? YELLOW : RED;
                questView.add(new GUIText(GUI, "\n"));
                questView.add(new GUIText(GUI, viewedQuest, c[0], c[1], c[2]));
                questView.add(new GUIText(GUI, "\n\n"));

                for (CObjective objective : objectives)
                {
                    c = objective.isDone() ? GREEN : objective.isStarted() ? YELLOW : RED;
                    questView.add(new GUIText(GUI, objective.getFullText(), c[0]));
                    questView.add(new GUIText(GUI, "\n"));
                }


                //Active quest buttons
                questView.add(new GUIText(GUI, "\n\n\n"));
                questView.add(new GUITextButton(GUI, viewedQuest.equals(QuestTracker.questname) ? "Stop Tracking" : "Start Tracking"));
                questView.add(new GUIText(GUI, "\n"));
                questView.add(new GUITextButton(GUI, "Abandon"));
                questView.add(new GUIText(GUI, "\n"));


                //Set navigator focus
                navigator.setActiveTab(0);

                for (GUITextSpoiler group : completedGroupToString.keySet())
                {
                    group.hide();
                }

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

                Color[] c = BLUE;
                questView.add(new GUIText(GUI, viewedQuest, c[0], c[1], c[2]));
                questView.add(new GUIText(GUI, "\n\n"));

                questView.add(new GUIText(GUI, "Quest Completed!", BLUE[0]));
                questView.add(new GUIText(GUI, "\n"));


                //Set navigator focus
                navigator.setActiveTab(1);

                for (GUITextSpoiler group : inProgressGroupToString.keySet())
                {
                    group.hide();
                }

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

        Class cls = element.getClass();
        if (cls == GUITextButton.class)
        {
            switch (element.toString())
            {
                case "Start Tracking":
                    Network.WRAPPER.sendToServer(new Network.RequestTrackerChangePacket(viewedQuest));
                    break;

                case "Stop Tracking":
                    Network.WRAPPER.sendToServer(new Network.RequestTrackerChangePacket(""));
                    break;

                case "Abandon":
                    //TODO make new abandon quest request packet
                    break;
            }
        }
        else if (cls == GUIText.class)
        {
            GUIText text = (GUIText) element;

            int index = questView.indexOf(text);
            if (index != -1)
            {
                if (index == 1) setViewedQuest(viewedQuest);
                return;
            }

            String questName = inProgressQuestToString.get(text);
            if (questName != null)
            {
                setViewedQuest(questName);
                return;
            }

            questName = completedQuestToString.get(text);
            if (questName != null) setViewedQuest(questName);
        }
        else if (cls == GUITextSpoiler.class)
        {
            if (inProgress.indexOf(element) != -1 || completed.indexOf(element) != -1)
            {
                GUITextSpoiler spoiler = (GUITextSpoiler) element;

                String group = inProgressGroupToString.get(spoiler);
                if (group != null)
                {
                    LinkedHashMap<String, ArrayList<CObjective>> quests = data.inProgressQuests.get(group);
                    if (quests != null && quests.size() > 0)
                    {
                        setViewedQuest(quests.keySet().toArray(new String[0])[0]);
                        event.setCanceled(true); //Because otherwise it will toggle the spoiler closed again
                    }
                }
                else
                {
                    group = completedGroupToString.get(spoiler);
                    if (group != null)
                    {
                        ArrayList<String> quests = data.completedQuests.get(group);
                        if (quests != null && quests.size() > 0)
                        {
                            setViewedQuest(quests.get(0));
                            event.setCanceled(true); //Because otherwise it will toggle the spoiler closed again
                        }
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
        questView.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);
        guiElements.add(questView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, questView));
    }
}

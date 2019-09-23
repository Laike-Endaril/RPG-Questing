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
            BLUE = new Color[]{Color.BLUE.copy().setVF(0.5f), Color.BLUE.copy().setVF(0.75f), Color.WHITE},
            PURPLE = new Color[]{Color.PURPLE.copy().setVF(0.5f), Color.PURPLE.copy().setVF(0.75f), Color.WHITE},
            WHITE = new Color[]{Color.WHITE.copy().setVF(0.5f), Color.WHITE.copy().setVF(0.75f), Color.WHITE};

    public static JournalGUI GUI;
    public static CPlayerQuestData data;
    public static String viewedQuest = "";
    private static GUITabView navigator;
    private static GUIScrollView inProgressTab, completedTab, allTab = null, questView;
    private static LinkedHashMap<GUIText, String> inProgressQuestElementToString = new LinkedHashMap<>();
    private static LinkedHashMap<GUIText, String> completedQuestElementToString = new LinkedHashMap<>();
    private static LinkedHashMap<String, GUIText> inProgressStringToQuestElement = new LinkedHashMap<>();
    private static LinkedHashMap<String, GUIText> completedStringToQuestElement = new LinkedHashMap<>();
    private static LinkedHashMap<GUITextSpoiler, String> inProgressGroupElementToString = new LinkedHashMap<>();
    private static LinkedHashMap<GUITextSpoiler, String> completedGroupElementToString = new LinkedHashMap<>();
    private static LinkedHashMap<GUIText, CQuest> allQuestElementToQuest = new LinkedHashMap<>();
    private static ArrayList<GUITextSpoiler> allGroupElements = new ArrayList<>();
    private static GUIText viewTracked = null;

    static
    {
        GUI = new JournalGUI();
        MinecraftForge.EVENT_BUS.register(JournalGUI.class);
    }

    private JournalGUI()
    {
    }

    public static void show(CPlayerQuestData dataIn, String questToView, LinkedHashMap<String, LinkedHashMap<String, CQuest>> allQuests)
    {
        //Make sure GUI exists
        Minecraft.getMinecraft().displayGuiScreen(GUI);


        //Clear old data
        clear();


        //General pre-computation for new data
        if (dataIn != null) data = dataIn;
        if (data == null) return;

        LinkedHashMap<String, Boolean> knownQuestGroupCompletion = new LinkedHashMap<>();


        //Quests in progress
        inProgressTab.add(new GUIText(GUI, "\n"));

        //Add "Go to tracked quest" button, if there is a tracked quest
        if (QuestTracker.questname.equals("")) viewTracked = null;
        else
        {
            viewTracked = new GUIText(GUI, " (View Tracked Quest)\n", PURPLE[0], PURPLE[1], PURPLE[2]);
            inProgressTab.add(viewTracked);
            inProgressTab.add(new GUIText(GUI, "\n"));
        }

        for (Map.Entry<String, LinkedHashMap<String, ArrayList<CObjective>>> entry2 : data.inProgressQuests.entrySet())
        {
            boolean groupDone = true;

            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, " " + entry2.getKey());
            inProgressGroupElementToString.put(groupSpoiler, entry2.getKey());
            inProgressTab.add(groupSpoiler);

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
                inProgressQuestElementToString.put(quest, entry.getKey());
                inProgressStringToQuestElement.put(entry.getKey(), quest);
            }

            knownQuestGroupCompletion.put(entry2.getKey(), groupDone);

            Color[] c = groupDone ? GREEN : YELLOW;
            groupSpoiler.setColor(c[0], c[1], c[2]);

            groupSpoiler.add(0, new GUIText(GUI, "\n==============================================================================================", c[0]));
            groupSpoiler.add(new GUIText(GUI, "\n==============================================================================================\n\n", c[0]));

            inProgressTab.add(new GUIText(GUI, "\n"));
        }
        if (inProgressTab.size() > 0) inProgressTab.remove(inProgressTab.size() - 1);


        //Completed quests
        completedTab.add(new GUIText(GUI, "\n"));
        for (Map.Entry<String, ArrayList<String>> entry : data.completedQuests.entrySet())
        {
            Boolean groupDone = knownQuestGroupCompletion.get(entry.getKey());
            Color[] c = groupDone == null ? BLUE : groupDone ? GREEN : YELLOW;

            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, " " + entry.getKey(), c[0], c[1], c[2]);
            completedGroupElementToString.put(groupSpoiler, entry.getKey());
            completedTab.add(groupSpoiler);

            for (String s : entry.getValue())
            {
                groupSpoiler.add(new GUIText(GUI, "\n"));

                Color[] c1 = inProgressStringToQuestElement.containsKey(s) ? GREEN : BLUE;
                GUIText quest = new GUIText(GUI, " * " + s + "\n", c1[0], c1[1], c1[2]);
                groupSpoiler.add(quest);
                completedQuestElementToString.put(quest, s);
                completedStringToQuestElement.put(s, quest);
            }

            groupSpoiler.add(0, new GUIText(GUI, "\n==============================================================================================", c[0]));
            groupSpoiler.add(new GUIText(GUI, "\n==============================================================================================\n\n", c[0]));

            completedTab.add(new GUIText(GUI, "\n"));
        }
        if (completedTab.size() > 0) completedTab.remove(completedTab.size() - 1);


        //Currently selected quest
        if (questToView.equals("")) questToView = QuestTracker.questname;
        setQuestView(questToView);


        //All quests (remove tab if not in edit mode; add tab and populate if in edit mode)
        if (allQuests == null)
        {
            if (navigator.tabs.size() == 3) navigator.removeTab(2);
        }
        else
        {
            if (navigator.tabs.size() == 2) navigator.addTab("All");

            allTab = new GUIScrollView(GUI, 0.96, 1);
            navigator.tabViews.get(2).add(allTab);
            navigator.tabViews.get(2).add(new GUIVerticalScrollbar(GUI, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, allTab));

            allTab.add(new GUIText(GUI, "\n"));
            for (Map.Entry<String, LinkedHashMap<String, CQuest>> entry : allQuests.entrySet())
            {
                GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, " " + entry.getKey(), WHITE[0], WHITE[1], WHITE[2]);
                allTab.add(groupSpoiler);
                allGroupElements.add(groupSpoiler);

                for (Map.Entry<String, CQuest> entry2 : entry.getValue().entrySet())
                {
                    groupSpoiler.add(new GUIText(GUI, "\n"));

                    GUIText quest = new GUIText(GUI, " * " + entry2.getKey() + "\n", WHITE[0], WHITE[1], WHITE[2]);
                    groupSpoiler.add(quest);

                    allQuestElementToQuest.put(quest, entry2.getValue());
                }

                groupSpoiler.add(0, new GUIText(GUI, "\n==============================================================================================", WHITE[0]));
                groupSpoiler.add(new GUIText(GUI, "\n==============================================================================================\n\n", WHITE[0]));

                allTab.add(new GUIText(GUI, "\n"));
            }
            if (allTab.size() > 0) allTab.remove(allTab.size() - 1);
        }
    }

    public static void clear()
    {
        data = null;
        viewedQuest = null;


        if (GUI == null || !GUI.isInitialized()) return;


        inProgressGroupElementToString.clear();
        completedGroupElementToString.clear();

        inProgressQuestElementToString.clear();
        completedQuestElementToString.clear();
        inProgressStringToQuestElement.clear();
        completedStringToQuestElement.clear();

        inProgressTab.clear();
        completedTab.clear();
        questView.clear();

        allTab = null;
        allQuestElementToQuest.clear();
        allGroupElements.clear();
        if (navigator.tabs.size() == 3) navigator.removeTab(2);
    }

    public static void setQuestView(CQuest quest)
    {
        if (questView == null) return;


        questView.clear();

        //Add quest name
        questView.add(new GUIText(GUI, "\n"));
        questView.add(new GUIText(GUI, quest.name.value, WHITE[0], WHITE[1], WHITE[2]));
        questView.add(new GUIText(GUI, "\n\n"));


        //Add objectives
        for (CObjective objective : quest.objectives)
        {
            questView.add(new GUIText(GUI, objective.getFullText(), WHITE[0]));
            questView.add(new GUIText(GUI, "\n"));
        }


        //Add quest buttons
        questView.add(new GUIText(GUI, "\n\n\n"));
        questView.add(new GUITextButton(GUI, viewedQuest.equals(QuestTracker.questname) ? "Stop Tracking" : "Start Tracking"));
        questView.add(new GUIText(GUI, "\n"));
        questView.add(new GUITextButton(GUI, "Abandon"));
        questView.add(new GUIText(GUI, "\n"));
    }

    public static void setQuestView(String questName)
    {
        viewedQuest = questName;
        if (viewedQuest == null) viewedQuest = "";


        if (questView == null) return;


        questView.clear();


        if (data == null) return;


        //Search in-progress quests
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<CObjective>>> entry : data.inProgressQuests.entrySet())
        {
            ArrayList<CObjective> objectives = entry.getValue().get(viewedQuest);
            if (objectives != null)
            {
                //Quest found


                //Find color
                boolean done = true, started = false;
                for (CObjective objective : objectives)
                {
                    if (!objective.isDone()) done = false;
                    if (objective.isStarted()) started = true;
                    if (started && !done) break;
                }
                Color[] c = done ? GREEN : started ? YELLOW : RED;


                //Add quest name
                questView.add(new GUIText(GUI, "\n"));
                questView.add(new GUIText(GUI, viewedQuest, c[0], c[1], c[2]));
                questView.add(new GUIText(GUI, "\n\n"));


                //Add objectives
                for (CObjective objective : objectives)
                {
                    c = objective.isDone() ? GREEN : objective.isStarted() ? YELLOW : RED;
                    questView.add(new GUIText(GUI, objective.getFullText(), c[0]));
                    questView.add(new GUIText(GUI, "\n"));
                }


                //Add quest buttons
                questView.add(new GUIText(GUI, "\n\n\n"));
                questView.add(new GUITextButton(GUI, viewedQuest.equals(QuestTracker.questname) ? "Stop Tracking" : "Start Tracking"));
                questView.add(new GUIText(GUI, "\n"));
                questView.add(new GUITextButton(GUI, "Abandon"));
                questView.add(new GUIText(GUI, "\n"));


                return;
            }
        }


        //Search completed quests
        for (Map.Entry<String, ArrayList<String>> entry : data.completedQuests.entrySet())
        {
            if (entry.getValue().contains(viewedQuest))
            {
                //Quest found


                //Set color
                Color[] c = BLUE;


                //Add quest name
                questView.add(new GUIText(GUI, "\n"));
                questView.add(new GUIText(GUI, viewedQuest, c[0], c[1], c[2]));
                questView.add(new GUIText(GUI, "\n\n"));


                //Add completion note
                questView.add(new GUIText(GUI, "Quest Completed!", BLUE[0]));
                questView.add(new GUIText(GUI, "\n"));


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
                    Network.WRAPPER.sendToServer(new Network.RequestAbandonQuestPacket(viewedQuest));
                    break;
            }
        }
        else if (cls == GUIText.class)
        {
            GUIText text = (GUIText) element;

            int index = questView.indexOf(text);
            if (index != -1)
            {
                if (index == 1)
                {
                    GUIText quest = inProgressStringToQuestElement.get(viewedQuest);
                    if (quest != null)
                    {
                        for (GUITextSpoiler group : inProgressGroupElementToString.keySet())
                        {
                            if (group.indexOf(quest) != -1) group.show();
                            else group.hide();
                        }
                        inProgressTab.focus(quest);
                        navigator.setActiveTab(0);
                    }

                    quest = completedStringToQuestElement.get(viewedQuest);
                    if (quest != null)
                    {
                        for (GUITextSpoiler group : completedGroupElementToString.keySet())
                        {
                            if (group.indexOf(quest) != -1) group.show();
                            else group.hide();
                        }
                        completedTab.focus(quest);
                        navigator.setActiveTab(1);
                    }
                }
            }
            else
            {
                if (allTab.indexOf(element) != -1)
                {
                    CQuest quest = allQuestElementToQuest.get(element);
                    if (quest != null) setQuestView(quest);
                }
                else
                {
                    String questName;
                    if (element == viewTracked) questName = QuestTracker.questname;
                    else
                    {
                        questName = inProgressQuestElementToString.get(text);
                        if (questName == null) questName = completedQuestElementToString.get(text);
                    }

                    if (questName != null) setQuestView(questName);
                }
            }
        }
        else if (cls == GUITextSpoiler.class)
        {
            if (inProgressTab.indexOf(element) != -1 || completedTab.indexOf(element) != -1)
            {
                GUITextSpoiler group = (GUITextSpoiler) element;

                String groupName = inProgressGroupElementToString.get(group);
                if (groupName != null)
                {
                    for (GUITextSpoiler spoiler : inProgressGroupElementToString.keySet())
                    {
                        if (spoiler != group) spoiler.hide();
                    }
                    group.toggle();
                    inProgressTab.focus(group);
                    event.setCanceled(true); //Because otherwise it will toggle the spoiler closed again
                }
                else
                {
                    groupName = completedGroupElementToString.get(group);
                    if (groupName != null)
                    {
                        for (GUITextSpoiler spoiler : inProgressGroupElementToString.keySet())
                        {
                            if (spoiler != group) spoiler.hide();
                        }
                        group.toggle();
                        completedTab.focus(group);
                        event.setCanceled(true); //Because otherwise it will toggle the spoiler closed again
                    }
                }
            }
            else if (allTab.indexOf(element) != -1)
            {
                GUITextSpoiler group = (GUITextSpoiler) element;

                for (GUITextSpoiler spoiler : allGroupElements)
                {
                    if (spoiler != group) spoiler.hide();
                }
                group.toggle();
                inProgressTab.focus(group);
                event.setCanceled(true); //Because otherwise it will toggle the spoiler closed again
            }
        }
    }

    @Override
    protected void init()
    {
        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        navigator = new GUITabView(this, 0, 0, 0.5, 1, "In Progress", "Completed");
        guiElements.add(navigator);


        inProgressTab = new GUIScrollView(this, 0.96, 1);
        navigator.tabViews.get(0).add(inProgressTab);
        navigator.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, inProgressTab));


        completedTab = new GUIScrollView(this, 0.96, 1);
        navigator.tabViews.get(1).add(completedTab);
        navigator.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, completedTab));


        questView = new GUIScrollView(this, 0.5, 0, 0.48, 1);
        questView.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);
        guiElements.add(questView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, questView));
    }
}

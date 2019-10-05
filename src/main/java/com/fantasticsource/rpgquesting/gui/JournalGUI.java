package com.fantasticsource.rpgquesting.gui;

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
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.QuestTracker;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;

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

    public static final JournalGUI GUI = new JournalGUI();
    public static CPlayerQuestData data = null;
    public static String viewedQuest = "";
    private static CQuest viewedEditable = null;
    private static GUITabView navigator;
    private static GUIScrollView inProgressTab, completedTab, allTab = null, questView;
    private static LinkedHashMap<GUIText, String> inProgressQuestElementToName = new LinkedHashMap<>();
    private static LinkedHashMap<GUIText, String> completedQuestElementToName = new LinkedHashMap<>();
    private static LinkedHashMap<String, GUIText> inProgressStringToQuestElement = new LinkedHashMap<>();
    private static LinkedHashMap<String, GUIText> completedStringToQuestElement = new LinkedHashMap<>();
    private static LinkedHashMap<GUITextSpoiler, String> inProgressGroupElementToName = new LinkedHashMap<>();
    private static LinkedHashMap<GUITextSpoiler, String> completedGroupElementToName = new LinkedHashMap<>();
    private static LinkedHashMap<GUIText, CQuest> allQuestElementToQuest = new LinkedHashMap<>();
    private static LinkedHashMap<String, GUITextSpoiler> allNameToGroupElement = new LinkedHashMap<>();
    private static GUIText viewTracked = null;

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
            viewTracked = new GUIText(GUI, "(View Tracked Quest)\n", PURPLE[0], PURPLE[1], PURPLE[2]);
            inProgressTab.add(viewTracked);
            inProgressTab.add(new GUIText(GUI, "\n"));
        }

        for (Map.Entry<String, LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>>> entry2 : data.inProgressQuests.entrySet())
        {
            boolean groupDone = true;

            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, entry2.getKey());
            inProgressGroupElementToName.put(groupSpoiler, entry2.getKey());
            inProgressTab.add(groupSpoiler.addClickActions(() -> ownedGroupAction(groupSpoiler)));

            for (Map.Entry<String, Pair<CUUID, ArrayList<CObjective>>> entry : entry2.getValue().entrySet())
            {
                groupSpoiler.add(new GUIText(GUI, "\n"));

                boolean done = true, started = false;
                for (CObjective objective : entry.getValue().getValue())
                {
                    if (!objective.isDone()) done = false;
                    if (objective.isStarted()) started = true;
                    if (started && !done) break;
                }
                if (!done) groupDone = false;

                Color[] c = done ? GREEN : started ? YELLOW : RED;
                GUIText questElement = new GUIText(GUI, "* " + entry.getKey() + "\n", c[0], c[1], c[2]);
                groupSpoiler.add(questElement.addClickActions(() -> ownedQuestAction(questElement)));
                inProgressQuestElementToName.put(questElement, entry.getKey());
                inProgressStringToQuestElement.put(entry.getKey(), questElement);
            }

            knownQuestGroupCompletion.put(entry2.getKey(), groupDone);

            Color[] c = groupDone ? GREEN : YELLOW;
            groupSpoiler.setColor(c[0], c[1], c[2]);

            groupSpoiler.add(0, new GUIText(GUI, "\n==============================================================================================", c[0]));
            groupSpoiler.add(new GUIText(GUI, "\n==============================================================================================\n\n", c[0]));

            inProgressTab.add(new GUIText(GUI, "\n"));
        }


        //Completed quests
        completedTab.add(new GUIText(GUI, "\n"));
        for (Map.Entry<String, ArrayList<String>> entry : data.completedQuests.entrySet())
        {
            Boolean groupDone = knownQuestGroupCompletion.get(entry.getKey());
            Color[] c = groupDone == null ? BLUE : groupDone ? GREEN : YELLOW;

            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, entry.getKey(), c[0], c[1], c[2]);
            completedGroupElementToName.put(groupSpoiler, entry.getKey());
            completedTab.add(groupSpoiler.addClickActions(() -> ownedGroupAction(groupSpoiler)));

            for (String s : entry.getValue())
            {
                groupSpoiler.add(new GUIText(GUI, "\n"));

                Color[] c1 = inProgressStringToQuestElement.containsKey(s) ? GREEN : BLUE;
                GUIText questElement = new GUIText(GUI, "* " + s + "\n", c1[0], c1[1], c1[2]);
                groupSpoiler.add(questElement.addClickActions(() -> ownedQuestAction(questElement)));
                completedQuestElementToName.put(questElement, s);
                completedStringToQuestElement.put(s, questElement);
            }

            groupSpoiler.add(0, new GUIText(GUI, "\n==============================================================================================", c[0]));
            groupSpoiler.add(new GUIText(GUI, "\n==============================================================================================\n\n", c[0]));

            completedTab.add(new GUIText(GUI, "\n"));
        }


        //Currently selected quest
        if (questToView.equals("")) questToView = QuestTracker.questname;
        setQuestViewProgressMode(questToView);


        //All quests (remove tab if not in edit mode; add tab and populate if in edit mode)
        if (allQuests == null)
        {
            if (navigator.tabs.size() == 3) navigator.removeTab(2);
        }
        else
        {
            if (navigator.tabs.size() == 2) navigator.addTab("All");

            allTab = new GUIScrollView(GUI, 0.04, 0, 0.88, 1);
            navigator.tabViews.get(2).add(allTab);
            navigator.tabViews.get(2).add(new GUIVerticalScrollbar(GUI, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, allTab));

            allTab.add(new GUIText(GUI, "\n"));
            for (Map.Entry<String, LinkedHashMap<String, CQuest>> entry : allQuests.entrySet())
            {
                GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, entry.getKey(), WHITE[0], WHITE[1], WHITE[2]);
                allTab.add(groupSpoiler.addClickActions(() ->
                {
                    for (GUITextSpoiler spoiler : allNameToGroupElement.values())
                    {
                        if (spoiler != groupSpoiler) spoiler.hide();
                    }

                    inProgressTab.focus(groupSpoiler);
                }));
                allNameToGroupElement.put(entry.getKey(), groupSpoiler);

                for (Map.Entry<String, CQuest> entry2 : entry.getValue().entrySet())
                {
                    groupSpoiler.add(new GUIText(GUI, "\n"));

                    GUIText questElement = new GUIText(GUI, "* " + entry2.getKey() + "\n", WHITE[0], WHITE[1], WHITE[2]);
                    groupSpoiler.add(questElement.addClickActions(() ->
                    {
                        CQuest quest = allQuestElementToQuest.get(questElement);
                        if (quest != null) setQuestViewEditMode(quest);
                    }));

                    allQuestElementToQuest.put(questElement, entry2.getValue());
                }

                groupSpoiler.add(0, new GUIText(GUI, "\n==============================================================================================", WHITE[0]));
                groupSpoiler.add(new GUIText(GUI, "\n==============================================================================================\n\n", WHITE[0]));

                allTab.add(new GUIText(GUI, "\n"));
            }
        }
    }

    public static void clear()
    {
        data = null;
        viewedQuest = "";
        viewedEditable = null;


        if (GUI == null || !GUI.isInitialized()) return;


        inProgressGroupElementToName.clear();
        completedGroupElementToName.clear();

        inProgressQuestElementToName.clear();
        completedQuestElementToName.clear();
        inProgressStringToQuestElement.clear();
        completedStringToQuestElement.clear();

        inProgressTab.clear();
        completedTab.clear();
        questView.clear();

        allTab = null;
        allQuestElementToQuest.clear();
        allNameToGroupElement.clear();
        if (navigator.tabs.size() == 3) navigator.removeTab(2);
    }

    public static void setQuestViewEditMode(CQuest quest)
    {
        if (questView == null) return;


        viewedEditable = quest;

        questView.clear();

        //Add quest name
        questView.add(new GUIText(GUI, "\n"));
        questView.add(new GUIText(GUI, quest.name.value, WHITE[0], WHITE[1], WHITE[2])).addClickActions(() ->
        {
            GUITextSpoiler group = allNameToGroupElement.get(viewedEditable.group.value);

            for (GUITextSpoiler spoiler : allNameToGroupElement.values())
            {
                spoiler.hide();
            }

            group.show();
            allTab.focus(group);
            navigator.setActiveTab(2);
        });
        questView.add(new GUIText(GUI, "\n\n"));


        //Add objectives
        for (CObjective objective : quest.objectives)
        {
            questView.add(new GUIText(GUI, objective.getFullText(), WHITE[0]));
            questView.add(new GUIText(GUI, "\n"));
        }


        //Add quest buttons
        questView.add(new GUIText(GUI, "\n\n\n"));
        questView.add(new GUITextButton(GUI, "Edit Quest").addClickActions(() -> QuestEditorGUI.show(viewedEditable)));
        questView.add(new GUIText(GUI, "\n"));
    }

    public static void setQuestViewProgressMode(String questName)
    {
        viewedQuest = questName;
        if (viewedQuest == null) viewedQuest = "";


        if (questView == null) return;


        questView.clear();


        if (data == null) return;


        //Search in-progress quests
        for (Map.Entry<String, LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>>> entry : data.inProgressQuests.entrySet())
        {
            ArrayList<CObjective> objectives = entry.getValue().get(viewedQuest).getValue();
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
                questView.add(new GUIText(GUI, viewedQuest, c[0], c[1], c[2]).addClickActions(() ->
                {
                    GUIText quest = inProgressStringToQuestElement.get(viewedQuest);
                    if (quest != null)
                    {
                        for (GUITextSpoiler group : inProgressGroupElementToName.keySet())
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
                        for (GUITextSpoiler group : completedGroupElementToName.keySet())
                        {
                            if (group.indexOf(quest) != -1) group.show();
                            else group.hide();
                        }
                        completedTab.focus(quest);
                        navigator.setActiveTab(1);
                    }
                }));
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
                if (viewedQuest.equals(QuestTracker.questname))
                {
                    questView.add(new GUITextButton(GUI, "Stop Tracking").addClickActions(() -> Network.WRAPPER.sendToServer(new Network.RequestTrackerChangePacket(""))));
                }
                else
                {
                    questView.add(new GUITextButton(GUI, "Start Tracking").addClickActions(() -> Network.WRAPPER.sendToServer(new Network.RequestTrackerChangePacket(viewedQuest))));
                }
                questView.add(new GUIText(GUI, "\n"));
                questView.add(new GUITextButton(GUI, "Abandon").addClickActions(() -> Network.WRAPPER.sendToServer(new Network.RequestAbandonQuestPacket(viewedQuest))));
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

    private static void ownedQuestAction(GUIText text)
    {
        String questName;
        if (text == viewTracked) questName = QuestTracker.questname;
        else
        {
            questName = inProgressQuestElementToName.get(text);
            if (questName == null) questName = completedQuestElementToName.get(text);
        }

        if (questName != null) setQuestViewProgressMode(questName);
    }

    private static void ownedGroupAction(GUITextSpoiler spoiler)
    {
        String groupName = inProgressGroupElementToName.get(spoiler);
        if (groupName != null)
        {
            for (GUITextSpoiler spoiler2 : inProgressGroupElementToName.keySet())
            {
                if (spoiler2 != spoiler) spoiler2.hide();
            }

            inProgressTab.focus(spoiler);
        }
        else
        {
            groupName = completedGroupElementToName.get(spoiler);
            if (groupName != null)
            {
                for (GUITextSpoiler spoiler2 : inProgressGroupElementToName.keySet())
                {
                    if (spoiler2 != spoiler) spoiler2.hide();
                }

                completedTab.focus(spoiler);
            }
        }
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        navigator = new GUITabView(this, 0, 0, 0.5, 1, "In Progress", "Completed");
        root.add(navigator);


        inProgressTab = new GUIScrollView(this, 0.04, 0, 0.88, 1);
        navigator.tabViews.get(0).add(inProgressTab);
        navigator.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, inProgressTab));


        completedTab = new GUIScrollView(this, 0.04, 0, 0.88, 1);
        navigator.tabViews.get(1).add(completedTab);
        navigator.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, completedTab));


        questView = new GUIScrollView(this, 0.5, 0, 0.48, 1);
        questView.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);
        root.add(questView);
        root.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, questView));
    }
}

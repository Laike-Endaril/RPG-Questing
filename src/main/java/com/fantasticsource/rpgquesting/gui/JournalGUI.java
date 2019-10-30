package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.MCTimestamp;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpoiler;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Colors;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuestTimestamp;
import com.fantasticsource.rpgquesting.quest.QuestTracker;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.rpgquesting.Colors.*;

public class JournalGUI extends GUIScreen
{
    public static final JournalGUI GUI = new JournalGUI(RPGQuesting.TEXT_SCALE);
    public static CPlayerQuestData data = null;
    public static String viewedQuest = "";
    private static GUITabView navigator;
    private static GUIScrollView inProgressTab, completedTab, questView;
    private static LinkedHashMap<GUIText, String> inProgressQuestElementToName = new LinkedHashMap<>();
    private static LinkedHashMap<GUIText, String> completedQuestElementToName = new LinkedHashMap<>();
    private static LinkedHashMap<String, GUIText> inProgressStringToQuestElement = new LinkedHashMap<>();
    private static LinkedHashMap<String, GUIText> completedStringToQuestElement = new LinkedHashMap<>();
    private static LinkedHashMap<GUITextSpoiler, String> inProgressGroupElementToName = new LinkedHashMap<>();
    private static LinkedHashMap<GUITextSpoiler, String> completedGroupElementToName = new LinkedHashMap<>();
    private static GUIText viewTracked = null;

    private JournalGUI(double textScale)
    {
        super(textScale);
    }

    public static void show(CPlayerQuestData dataIn, String questToView)
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
        setQuestView(questToView);
    }

    public static void clear()
    {
        data = null;
        viewedQuest = "";


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

        if (navigator.tabs.size() == 3) navigator.removeTab(2);
    }

    public static void setQuestView(String questName)
    {
        viewedQuest = questName;
        if (viewedQuest == null) viewedQuest = "";


        if (questView == null) return;


        questView.clear();


        if (data == null) return;


        //Search in-progress quests
        for (Map.Entry<String, LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>>> entry : data.inProgressQuests.entrySet())
        {
            Pair<CUUID, ArrayList<CObjective>> questEntry = entry.getValue().get(viewedQuest);
            if (questEntry == null) continue;


            ArrayList<CObjective> objectives = questEntry.getValue();
            if (objectives == null) continue;


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
                CQuestTimestamp completionData = data.completionData.get(questName);
                MCTimestamp timestamp = completionData.timestamp.value;
                questView.add(new GUIText(GUI, "Completed on " + timestamp.toString(true, false, false).replaceAll("-", "/"), BLUE[0]));
                questView.add(new GUIText(GUI, "At " + timestamp.toString(false, true, false), BLUE[0]));
                questView.add(new GUIText(GUI, "\n"));
                questView.add(new GUIText(GUI, "On in-game day " + timestamp.getGameYear() + " / " + timestamp.getGameMonth() + " / " + timestamp.getGameDay(), BLUE[0]));
                int minute = timestamp.getGameMinute();
                int second = timestamp.getGameSecond();
                questView.add(new GUIText(GUI, "At " + timestamp.getGameHour() + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second), BLUE[0]));
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

        if (questName != null) setQuestView(questName);
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
    public String title()
    {
        return "Journal";
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));

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

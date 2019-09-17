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
    private static final Color
            RED = Color.RED.copy().setVF(0.5f),
            YELLOW = Color.YELLOW.copy().setVF(0.5f),
            GREEN = Color.GREEN.copy().setVF(0.5f),
            BLUE = Color.BLUE.copy().setVF(0.5f);

    public static JournalGUI GUI;
    public static boolean editable = false;
    private static CPlayerQuestData data;
    private static GUITabView navigator;
    private static GUIScrollView inProgress, completed, questView;

    private static LinkedHashMap<GUIText, String> inProgressQuestMap = new LinkedHashMap<>();
    private static LinkedHashMap<GUIText, String> completedQuestMap = new LinkedHashMap<>();

    private static String viewedQuest;

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
        inProgressQuestMap.clear();
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<CObjective>>> entry2 : data.inProgressQuests.entrySet())
        {
            boolean groupDone = true;

            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, entry2.getKey().toUpperCase() + "\n");
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
                inProgressQuestMap.put(quest, entry.getKey());
            }

            knownQuestGroupCompletion.put(entry2.getKey(), groupDone);

            Color c = groupDone ? GREEN : YELLOW;
            groupSpoiler.setColor(c, c.copy().setVF(0.75f), Color.WHITE);
            inProgress.add(new GUIText(GUI, "\n"));
        }
        if (inProgress.size() > 0) inProgress.remove(inProgress.size() - 1);


        //Complated quests
        completed.clear();
        completedQuestMap.clear();
        for (Map.Entry<String, ArrayList<String>> entry : data.completedQuests.entrySet())
        {
            Boolean groupDone = knownQuestGroupCompletion.get(entry.getKey());
            Color c = (groupDone == null || groupDone) ? GREEN : YELLOW;

            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, entry.getKey().toUpperCase() + "\n", c, c.copy().setVF(0.75f), Color.WHITE);
            completed.add(groupSpoiler);

            for (String s : entry.getValue())
            {
                GUIText quest = new GUIText(GUI, "* " + s + "\n", BLUE);
                completed.add(quest);
                completedQuestMap.put(quest, s);
            }
            groupSpoiler.add(new GUIText(GUI, "\n"));
        }
        if (completed.size() > 0) completed.remove(completed.size() - 1);


        //Currently selected quest
        setCurrentJournalQuest(packet.selectedQuest.value);
    }

    public static void clear()
    {
        editable = false;
        data = null;
        inProgressQuestMap.clear();
        completedQuestMap.clear();

        inProgress.clear();
        completed.clear();
        questView.clear();
    }

    public static void setCurrentJournalQuest(String questName)
    {
        if (questName != null && !questName.equals("")) viewedQuest = questName;
        updateQuestView();
    }

    public static void updateQuestView()
    {
        for (Map.Entry<String, ArrayList<String>> entry : data.completedQuests.entrySet())
        {
            if (entry.getValue().contains(viewedQuest))
            {
                questView.clear();

                Color c = BLUE;
                questView.add(new GUIText(GUI, viewedQuest.toUpperCase() + "\n\n", c, c.copy().setVF(0.75f), Color.WHITE));

                questView.add(new GUIText(GUI, "* Quest Completed! *", BLUE));


                //TODO set navigator

                return;
            }
        }


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


                //TODO set navigator

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
            if (index == 0)
            {
                //TODO
            }
        }


        String questName = inProgressQuestMap.get(element);
        if (questName != null)
        {
            setCurrentJournalQuest(questName);
            return;
        }

        questName = completedQuestMap.get(element);
        if (questName != null)
        {
            setCurrentJournalQuest(questName);
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

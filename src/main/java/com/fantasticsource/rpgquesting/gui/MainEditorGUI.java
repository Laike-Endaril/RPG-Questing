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
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainEditorGUI extends GUIScreen
{
    public static final Color[]
            RED = new Color[]{Color.RED.copy().setVF(0.5f), Color.RED.copy().setVF(0.75f), Color.WHITE},
            YELLOW = new Color[]{Color.YELLOW.copy().setVF(0.5f), Color.YELLOW.copy().setVF(0.75f), Color.WHITE},
            GREEN = new Color[]{Color.GREEN.copy().setVF(0.5f), Color.GREEN.copy().setVF(0.75f), Color.WHITE},
            BLUE = new Color[]{Color.BLUE.copy().setVF(0.5f), Color.BLUE.copy().setVF(0.75f), Color.WHITE},
            PURPLE = new Color[]{Color.PURPLE.copy().setVF(0.5f), Color.PURPLE.copy().setVF(0.75f), Color.WHITE},
            WHITE = new Color[]{Color.WHITE.copy().setVF(0.5f), Color.WHITE.copy().setVF(0.75f), Color.WHITE};

    public static final MainEditorGUI GUI = new MainEditorGUI();
    public static CPlayerQuestData data = null;
    public static String viewedQuest = "";
    private static CQuest viewedEditable = null;
    private static GUITabView navigator;
    private static GUIScrollView questsTab = null, dialoguesTab = null, detailView;
    private static LinkedHashMap<GUIText, CQuest> allQuestElementToQuest = new LinkedHashMap<>();
    private static LinkedHashMap<String, GUITextSpoiler> allNameToGroupElement = new LinkedHashMap<>();

    private MainEditorGUI()
    {
    }

    public static void show(LinkedHashMap<String, LinkedHashMap<String, CQuest>> allQuests)
    {
        //Make sure GUI exists
        Minecraft.getMinecraft().displayGuiScreen(GUI);


        //Clear old data
        clear();


        //Quests
        questsTab = new GUIScrollView(GUI, 0.04, 0, 0.88, 1);
        navigator.tabViews.get(0).add(questsTab);
        navigator.tabViews.get(0).add(new GUIVerticalScrollbar(GUI, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, questsTab));

        {
            questsTab.add(new GUIText(GUI, "\n"));
            GUIText questElement = new GUIText(GUI, "(Create New Quest)\n", PURPLE[0], PURPLE[1], PURPLE[2]);
            questsTab.add(questElement.addClickActions(() -> QuestEditorGUI.GUI.show(new CQuest("", "", 1, false))));
        }

        questsTab.add(new GUIText(GUI, "\n"));
        for (Map.Entry<String, LinkedHashMap<String, CQuest>> entry : allQuests.entrySet())
        {
            GUITextSpoiler groupSpoiler = new GUITextSpoiler(GUI, entry.getKey(), WHITE[0], WHITE[1], WHITE[2]);
            questsTab.add(groupSpoiler.addClickActions(() ->
            {
                for (GUITextSpoiler spoiler : allNameToGroupElement.values())
                {
                    if (spoiler != groupSpoiler) spoiler.hide();
                }
            }));
            allNameToGroupElement.put(entry.getKey(), groupSpoiler);

            for (Map.Entry<String, CQuest> entry2 : entry.getValue().entrySet())
            {
                groupSpoiler.add(new GUIText(GUI, "\n"));

                GUIText questElement = new GUIText(GUI, "* " + entry2.getKey() + "\n", WHITE[0], WHITE[1], WHITE[2]);
                groupSpoiler.add(questElement.addClickActions(() ->
                {
                    CQuest quest = allQuestElementToQuest.get(questElement);
                    if (quest != null) setDetailView(quest);
                }));

                allQuestElementToQuest.put(questElement, entry2.getValue());
            }

            groupSpoiler.add(0, new GUIText(GUI, "\n==============================================================================================", WHITE[0]));
            groupSpoiler.add(new GUIText(GUI, "\n==============================================================================================\n\n", WHITE[0]));

            questsTab.add(new GUIText(GUI, "\n"));
        }
    }

    public static void clear()
    {
        data = null;
        viewedQuest = "";
        viewedEditable = null;


        if (GUI == null || !GUI.isInitialized()) return;


        detailView.clear();

        questsTab = null;
        allQuestElementToQuest.clear();
        allNameToGroupElement.clear();
    }

    public static void setDetailView(CQuest quest)
    {
        if (detailView == null) return;


        viewedEditable = quest;

        detailView.clear();

        //Add quest name
        detailView.add(new GUIText(GUI, "\n"));
        detailView.add(new GUIText(GUI, quest.name.value, WHITE[0], WHITE[1], WHITE[2])).addClickActions(() ->
        {
            GUITextSpoiler group = allNameToGroupElement.get(viewedEditable.group.value);

            for (GUITextSpoiler spoiler : allNameToGroupElement.values())
            {
                spoiler.hide();
            }

            group.show();
            questsTab.focus(group);
            navigator.setActiveTab(0);
        });
        detailView.add(new GUIText(GUI, "\n\n"));


        //Add objectives
        for (CObjective objective : quest.objectives)
        {
            detailView.add(new GUIText(GUI, objective.getFullText(), WHITE[0]));
            detailView.add(new GUIText(GUI, "\n"));
        }


        //Add quest buttons
        detailView.add(new GUIText(GUI, "\n\n\n"));
        detailView.add(new GUITextButton(GUI, "Edit Quest").addClickActions(() -> QuestEditorGUI.GUI.show(viewedEditable)));
        detailView.add(new GUIText(GUI, "\n"));
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        navigator = new GUITabView(this, 0, 0, 0.5, 1, "Quests", "Dialogues");
        root.add(navigator);


        detailView = new GUIScrollView(this, 0.5, 0, 0.48, 1);
        detailView.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);
        root.add(detailView);
        root.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, detailView));
    }
}

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

import static com.fantasticsource.rpgquesting.Colors.PURPLE;
import static com.fantasticsource.rpgquesting.Colors.WHITE;

public class MainEditorGUI extends GUIScreen
{
    public static CPlayerQuestData data = null;
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
        MainEditorGUI gui = new MainEditorGUI();

        //Make sure GUI exists
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Quests
        questsTab = new GUIScrollView(gui, 0.04, 0, 0.88, 1);
        navigator.tabViews.get(0).add(questsTab);
        navigator.tabViews.get(0).add(new GUIVerticalScrollbar(gui, 0.96, 0, 0.04, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, questsTab));

        {
            questsTab.add(new GUIText(gui, "\n"));
            GUIText questElement = new GUIText(gui, "(Create New Quest)\n", PURPLE[0], PURPLE[1], PURPLE[2]);
            questsTab.add(questElement.addClickActions(() -> QuestEditorGUI.GUI.show(new CQuest("", "", 1, false))));
        }

        questsTab.add(new GUIText(gui, "\n"));
        for (Map.Entry<String, LinkedHashMap<String, CQuest>> entry : allQuests.entrySet())
        {
            GUITextSpoiler groupSpoiler = new GUITextSpoiler(gui, entry.getKey(), WHITE[0], WHITE[1], WHITE[2]);
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
                groupSpoiler.add(new GUIText(gui, "\n"));

                GUIText questElement = new GUIText(gui, "* " + entry2.getKey() + "\n", WHITE[0], WHITE[1], WHITE[2]);
                groupSpoiler.add(questElement.addClickActions(() ->
                {
                    CQuest quest = allQuestElementToQuest.get(questElement);
                    if (quest != null) gui.setDetailView(quest);
                }));

                allQuestElementToQuest.put(questElement, entry2.getValue());
            }

            groupSpoiler.add(0, new GUIText(gui, "\n==============================================================================================", WHITE[0]));
            groupSpoiler.add(new GUIText(gui, "\n==============================================================================================\n\n", WHITE[0]));

            questsTab.add(new GUIText(gui, "\n"));
        }
    }

    public void setDetailView(CQuest quest)
    {
        if (detailView == null) return;


        viewedEditable = quest;

        detailView.clear();

        //Add quest name
        detailView.add(new GUIText(this, "\n"));
        detailView.add(new GUIText(this, quest.name.value, WHITE[0], WHITE[1], WHITE[2])).addClickActions(() ->
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
        detailView.add(new GUIText(this, "\n\n"));


        //Add objectives
        for (CObjective objective : quest.objectives)
        {
            detailView.add(new GUIText(this, objective.getFullText(), WHITE[0]));
            detailView.add(new GUIText(this, "\n"));
        }


        //Add quest buttons
        detailView.add(new GUIText(this, "\n\n\n"));
        detailView.add(new GUITextButton(this, "Edit Quest").addClickActions(() -> QuestEditorGUI.GUI.show(viewedEditable)));
        detailView.add(new GUIText(this, "\n"));
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

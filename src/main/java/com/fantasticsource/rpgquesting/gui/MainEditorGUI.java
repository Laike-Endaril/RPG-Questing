package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.text.GUITextSpoiler;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Network.EditorPacket;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.rpgquesting.Colors.*;

public class MainEditorGUI extends GUIScreen
{
    private static int lastTab = 0;

    private GUITabView navigator;
    private GUIScrollView questNav = null, dialogueNav = null;

    private LinkedHashMap<String, GUITextSpoiler> allNameToQuestGroupElement = new LinkedHashMap<>();

    private LinkedHashMap<String, GUITextSpoiler> allNameToDialogueGroupElement = new LinkedHashMap<>();

    private MainEditorGUI(double textScale)
    {
        super(textScale);
    }

    public static void show(EditorPacket packet)
    {
        MainEditorGUI gui = new MainEditorGUI(0.5);

        //Make sure GUI exists
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Quests
        {
            GUITextSpacer spacer = new GUITextSpacer(gui, true);
            gui.questNav = new GUIScrollView(gui, 0.98 - spacer.width * 2, 1);
            gui.navigator.tabViews.get(0).add(spacer.addRecalcActions(() -> gui.questNav.width = 0.98 - spacer.width * 2));
            gui.navigator.tabViews.get(0).add(gui.questNav);
            gui.navigator.tabViews.get(0).add(new GUIVerticalScrollbar(gui, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, gui.questNav));
        }

        {
            gui.questNav.add(new GUITextSpacer(gui));
            GUIText questElement = new GUIText(gui, "(Create New Quest)\n", PURPLE[0], PURPLE[1], PURPLE[2]);
            gui.questNav.add(questElement.addClickActions(() -> QuestEditorGUI.GUI.show(new CQuest("", "", 1, false))));
        }

        gui.questNav.add(new GUITextSpacer(gui));
        for (Map.Entry<String, LinkedHashMap<String, CQuest>> entry : packet.allQuests.entrySet())
        {
            GUITextSpoiler groupSpoiler = new GUITextSpoiler(gui, entry.getKey(), WHITE[0], WHITE[1], WHITE[2]);
            gui.questNav.add(groupSpoiler.addClickActions(() ->
            {
                for (GUITextSpoiler spoiler : gui.allNameToQuestGroupElement.values())
                {
                    if (spoiler != groupSpoiler) spoiler.hide();
                }
            }));
            gui.allNameToQuestGroupElement.put(entry.getKey(), groupSpoiler);

            for (Map.Entry<String, CQuest> entry2 : entry.getValue().entrySet())
            {
                groupSpoiler.add(new GUITextSpacer(gui));

                GUIText questElement = new GUIText(gui, "* " + entry2.getKey() + "\n", WHITE[0], WHITE[1], WHITE[2]);
                groupSpoiler.add(questElement.addClickActions(() -> QuestEditorGUI.GUI.show(entry2.getValue())));
            }

            groupSpoiler.add(0, new GUIText(gui, "\n==============================================================================================", WHITE[0]));
            groupSpoiler.add(new GUIText(gui, "\n==============================================================================================\n\n", WHITE[0]));

            gui.questNav.add(new GUITextSpacer(gui));
        }


        //Dialogues
        {
            GUITextSpacer spacer = new GUITextSpacer(gui, true);
            gui.dialogueNav = new GUIScrollView(gui, 0.98 - spacer.width * 2, 1);
            gui.navigator.tabViews.get(1).add(spacer.addRecalcActions(() -> gui.dialogueNav.width = 0.98 - spacer.width * 2));
            gui.navigator.tabViews.get(1).add(gui.dialogueNav);
            gui.navigator.tabViews.get(1).add(new GUIVerticalScrollbar(gui, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, gui.dialogueNav));
        }

        {
            gui.dialogueNav.add(new GUITextSpacer(gui));
            GUIText dialogueElement = new GUIText(gui, "(Create New Dialogue)\n", PURPLE[0], PURPLE[1], PURPLE[2]);
            gui.dialogueNav.add(dialogueElement.addClickActions(() -> DialogueEditorGUI.GUI.show(new CDialogue("", ""))));
        }

        gui.dialogueNav.add(new GUITextSpacer(gui));
        for (Map.Entry<String, LinkedHashMap<String, CDialogue>> entry : packet.allDialogues.entrySet())
        {
            GUITextSpoiler groupSpoiler = new GUITextSpoiler(gui, entry.getKey(), WHITE[0], WHITE[1], WHITE[2]);
            gui.dialogueNav.add(groupSpoiler.addClickActions(() ->
            {
                for (GUITextSpoiler spoiler : gui.allNameToDialogueGroupElement.values())
                {
                    if (spoiler != groupSpoiler) spoiler.hide();
                }
            }));
            gui.allNameToDialogueGroupElement.put(entry.getKey(), groupSpoiler);

            for (Map.Entry<String, CDialogue> entry2 : entry.getValue().entrySet())
            {
                groupSpoiler.add(new GUITextSpacer(gui));

                GUIText dialogueElement = new GUIText(gui, "* " + entry2.getKey() + "\n", WHITE[0], WHITE[1], WHITE[2]);
                groupSpoiler.add(dialogueElement.addClickActions(() -> DialogueEditorGUI.GUI.show(entry2.getValue())));
            }

            groupSpoiler.add(0, new GUIText(gui, "\n==============================================================================================", WHITE[0]));
            groupSpoiler.add(new GUIText(gui, "\n==============================================================================================\n\n", WHITE[0]));

            gui.dialogueNav.add(new GUITextSpacer(gui));
        }

        gui.navigator.setActiveTab(lastTab);
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, T_BLACK));

        navigator = new GUITabView(this, 1, 1, "All Quests", "All Dialogues");
        root.add(navigator);
    }

    @Override
    public void onClosed()
    {
        super.onClosed();

        lastTab = navigator.currentTab();
    }
}

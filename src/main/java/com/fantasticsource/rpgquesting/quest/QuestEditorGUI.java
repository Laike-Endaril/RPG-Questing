package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpoiler;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBoolean;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;
import java.util.UUID;

import static com.fantasticsource.rpgquesting.quest.JournalGUI.BLUE;
import static com.fantasticsource.rpgquesting.quest.JournalGUI.WHITE;

public class QuestEditorGUI extends GUIScreen
{
    public static QuestEditorGUI GUI;

    private static GUITextButton save, cancel, delete;
    private static GUIGradientBorder separator;
    private static GUITabView tabView;
    private static GUIScrollView main, objectives, rewards, conditions, dialogues;

    static
    {
        GUI = new QuestEditorGUI();
        MinecraftForge.EVENT_BUS.register(QuestEditorGUI.class);
    }

    public static void show(CQuest quest)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);


        //Main tab
        main.clear();

        main.add(new GUIText(GUI, "\n"));
        main.add(new GUILabeledTextInput(GUI, " Name: ", quest.name.value, FilterNotEmpty.INSTANCE));

        main.add(new GUIText(GUI, "\n"));
        main.add(new GUILabeledTextInput(GUI, " Group: ", quest.group.value, FilterNotEmpty.INSTANCE));

        main.add(new GUIText(GUI, "\n"));
        main.add(new GUILabeledTextInput(GUI, " Level: ", "" + quest.level.value, FilterInt.INSTANCE));

        main.add(new GUIText(GUI, "\n"));
        main.add(new GUILabeledTextInput(GUI, " Repeatable: ", "" + quest.repeatable.value, FilterBoolean.INSTANCE));

        main.add(new GUIText(GUI, "\n"));
        main.add(new GUILabeledTextInput(GUI, " Experience Awarded: ", "" + quest.experience.value, FilterInt.INSTANCE));

        main.add(new GUIText(GUI, "\n"));


        //Objectives tab
        objectives.clear();

        for (CObjective objective : quest.objectives)
        {
            objectives.add(new GUIText(GUI, "\n"));
            objectives.add(new GUIText(GUI, " " + objective.getFullText(), WHITE[0], WHITE[1], WHITE[2]));
        }
        objectives.add(new GUIText(GUI, "\n"));


        //Rewards tab
        rewards.clear();

        for (CItemStack reward : quest.rewards)
        {
            rewards.add(new GUIText(GUI, "\n"));
            rewards.add(new GUIText(GUI, " (" + reward.stack.getCount() + "x) " + reward.stack.getDisplayName(), WHITE[0], WHITE[1], WHITE[2]));
        }
        rewards.add(new GUIText(GUI, "\n"));


        //Conditions tab
        conditions.clear();

        if (conditions.size() == 0)
        {
            conditions.add(new GUIText(GUI, "\n"));
            conditions.add(new GUIText(GUI, " (None)", WHITE[0]));
        }
        else for (CCondition condition : quest.conditions)
        {
            conditions.add(new GUIText(GUI, "\n"));
            conditions.add(new GUIText(GUI, " " + condition.description(), WHITE[0], WHITE[1], WHITE[2]));
        }
        conditions.add(new GUIText(GUI, "\n"));


        //Dialogues tab
        dialogues.clear();

        LinkedHashMap<UUID, GUITextSpoiler> dialogueSpoilers = new LinkedHashMap<>();
        for (CRelatedDialogueEntry dialogueEntry : quest.relatedDialogues)
        {
            UUID id = dialogueEntry.dialogueID.value;
            GUITextSpoiler spoiler = dialogueSpoilers.get(id);
            if (spoiler == null)
            {
                spoiler = new GUITextSpoiler(GUI, " " + dialogueEntry.dialogueName.value, WHITE[0], WHITE[1], WHITE[2]);

                dialogues.add(new GUIText(GUI, "\n"));
                dialogues.add(spoiler);
                dialogueSpoilers.put(id, spoiler);

                spoiler.add(new GUIText(GUI, "\n"));
                spoiler.add(new GUIText(GUI, "========================================================================================================================================================================================", WHITE[0]));
                spoiler.add(new GUIText(GUI, "\n"));
                spoiler.add(new GUIText(GUI, " * ", WHITE[0]));
                spoiler.add(new GUIText(GUI, "Branch " + dialogueEntry.branchIndex.value, BLUE[0], BLUE[1], BLUE[2]));
                spoiler.add(new GUIText(GUI, " " + dialogueEntry.relation.value, WHITE[0]));
                spoiler.add(new GUIText(GUI, "\n"));
                spoiler.add(new GUIText(GUI, "========================================================================================================================================================================================", WHITE[0]));
                spoiler.add(new GUIText(GUI, "\n"));
            }
            else
            {
                spoiler.add(spoiler.size() - 2, new GUIText(GUI, " * ", WHITE[0]));
                spoiler.add(spoiler.size() - 2, new GUIText(GUI, "Branch " + dialogueEntry.branchIndex.value, BLUE[0], BLUE[1], BLUE[2]));
                spoiler.add(spoiler.size() - 2, new GUIText(GUI, " " + dialogueEntry.relation.value, WHITE[0]));
                spoiler.add(spoiler.size() - 2, new GUIText(GUI, "\n"));
            }
        }
        dialogues.add(new GUIText(GUI, "\n"));
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);
        tabView.height = 1 - (separator.y + separator.height);
        tabView.recalc();
    }

    @Override
    protected void init()
    {
        GUIElement root = new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f));
        guiElements.add(root);

        save = new GUITextButton(this, "Save and Close", JournalGUI.GREEN[0]);
        root.add(save);
        cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel);
        delete = new GUITextButton(this, "Delete Quest and Close", JournalGUI.RED[0]);
        root.add(delete);

        separator = new GUIGradientBorder(this, 1, 0.03, 0.3, Color.GRAY, Color.WHITE.copy().setAF(0.3f));
        root.add(separator);

        tabView = new GUITabView(this, 1, 1 - (separator.y + separator.height), "Main", "Objectives", "Rewards", "Availability Conditions", "Dialogues");
        root.add(tabView);

        main = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(0).add(main);
        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, main));

        objectives = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(1).add(objectives);
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectives));

        rewards = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(2).add(rewards);
        tabView.tabViews.get(2).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, rewards));

        conditions = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(3).add(conditions);
        tabView.tabViews.get(3).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditions));

        dialogues = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(4).add(dialogues);
        tabView.tabViews.get(4).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, dialogues));
    }

    @SubscribeEvent
    public static void click(GUILeftClickEvent event)
    {
        if (event.getScreen() != GUI) return;

        GUIElement element = event.getElement();
        if (element == save)
        {

        }
        else if (element == cancel)
        {
            GUI.close();
        }
        else if (element == delete)
        {

        }
        else
        {

        }
    }
}

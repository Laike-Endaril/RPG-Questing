package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBoolean;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import static com.fantasticsource.tools.datastructures.Color.WHITE;

public class QuestEditorGUI extends GUIScreen
{
    public static QuestEditorGUI GUI;

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
            objectives.add(new GUIText(GUI, " " + objective.getFullText(), getIdleColor(WHITE), getHoverColor(WHITE), WHITE));
        }
        objectives.add(new GUIText(GUI, "\n"));


        //Rewards tab
        rewards.clear();

        for (CItemStack reward : quest.rewards)
        {
            rewards.add(new GUIText(GUI, "\n"));
            rewards.add(new GUIText(GUI, " " + reward.stack.getDisplayName(), getIdleColor(WHITE), getHoverColor(WHITE), WHITE));
        }
        rewards.add(new GUIText(GUI, "\n"));


        //Conditions tab
        conditions.clear();

        for (CCondition condition : quest.conditions)
        {
            conditions.add(new GUIText(GUI, "\n"));
            conditions.add(new GUIText(GUI, " " + condition.getClass().getSimpleName(), getIdleColor(WHITE), getHoverColor(WHITE), WHITE));
        }
        conditions.add(new GUIText(GUI, "\n"));


        //Dialogues tab
        dialogues.clear();

        dialogues.add(new GUIText(GUI, "\n"));
        for (CRelatedDialogueEntry dialogueEntry : quest.relatedDialogues)
        {
            GUITextButton button = new GUITextButton(GUI, " " + dialogueEntry.relation.value + "\n " + dialogueEntry.dialogueName.value + "\n Branch " + dialogueEntry.branchIndex.value + "\n (Dialogue ID = " + dialogueEntry.dialogueID.value + ")");
            ((GUIGradientBorder) button.children.get(0)).
            dialogues.add(button);
        }
        dialogues.add(new GUIText(GUI, "\n"));
    }

    @Override
    protected void init()
    {
        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        tabView = new GUITabView(this, 1, 1, "Main", "Objectives", "Rewards", "Availability Conditions", "Dialogues");
        guiElements.add(tabView);

        main = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(0).add(main);
        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, WHITE, Color.BLANK, main));

        objectives = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(1).add(objectives);
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, WHITE, Color.BLANK, objectives));

        rewards = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(2).add(rewards);
        tabView.tabViews.get(2).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, WHITE, Color.BLANK, rewards));

        conditions = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(3).add(conditions);
        tabView.tabViews.get(3).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, WHITE, Color.BLANK, conditions));

        dialogues = new GUIScrollView(this, 0.98, 1);
        tabView.tabViews.get(4).add(dialogues);
        tabView.tabViews.get(4).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, WHITE, Color.BLANK, dialogues));
    }

    @Override
    public void onGuiClosed()
    {
        Network.WRAPPER.sendToServer(new Network.RequestJournalDataPacket());
    }
}

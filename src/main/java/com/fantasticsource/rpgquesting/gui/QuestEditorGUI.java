package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBoolean;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.mctools.gui.screen.ItemSelectionGUI;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CRelatedDialogueEntry;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedHashMap;
import java.util.UUID;

import static com.fantasticsource.rpgquesting.gui.JournalGUI.*;

public class QuestEditorGUI extends GUIScreen
{
    public static final QuestEditorGUI GUI = new QuestEditorGUI();
    public static GUIScrollView main, objectives, rewards, conditions, dialogues;
    private static GUITextButton save, cancel, delete;
    private static GUIGradientBorder separator;
    private static GUITabView tabView;

    public static void show(CQuest quest)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);


        //Main tab
        main.clear();

        main.add(new GUIText(GUI, "\n"));
        main.add(new GUILabeledTextInput(GUI, "Name: ", quest.name.value, FilterNotEmpty.INSTANCE));

        main.add(new GUIText(GUI, "\n"));
        main.add(new GUILabeledTextInput(GUI, "Group: ", quest.group.value, FilterNotEmpty.INSTANCE));

        main.add(new GUIText(GUI, "\n"));
        main.add(new GUILabeledTextInput(GUI, "Level: ", "" + quest.level.value, FilterInt.INSTANCE));

        main.add(new GUIText(GUI, "\n"));
        main.add(new GUILabeledTextInput(GUI, "Repeatable: ", "" + quest.repeatable.value, FilterBoolean.INSTANCE));

        main.add(new GUIText(GUI, "\n"));
        main.add(new GUILabeledTextInput(GUI, "Experience Awarded: ", "" + quest.experience.value, FilterInt.INSTANCE));

        main.add(new GUIText(GUI, "\n"));


        //Objectives tab
        objectives.clear();

        for (CObjective objective : quest.objectives)
        {
            objectives.add(new GUIText(GUI, "\n"));
            objectives.add(new GUIText(GUI, objective.getFullText(), WHITE[0], WHITE[1], WHITE[2]));
        }
        objectives.add(new GUIText(GUI, "\n"));


        //Rewards tab
        rewards.clear();

        for (CItemStack reward : quest.rewards)
        {
            rewards.add(new GUIText(GUI, "\n"));
            GUIItemStack rewardElement = new GUIItemStack(GUI, reward.stack);
            rewards.add(rewardElement.addClickActions(() ->
            {
                ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement);
                gui.addOnClosedActions(() -> GUI.editReward(rewardElement, gui.selection));
            }));
        }

        {
            rewards.add(new GUIText(GUI, "\n"));
            GUIItemStack rewardElement = new GUIItemStack(GUI, ItemStack.EMPTY);
            rewardElement.text = TextFormatting.DARK_PURPLE + "(Add new reward)";
            rewards.add(rewardElement.addClickActions(() ->
            {
                ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement);
                gui.addOnClosedActions(() -> GUI.editReward(rewardElement, gui.selection));
            }));
        }

        if (quest.rewards.size() > 0)
        {
            rewards.add(new GUIText(GUI, "\n"));
            rewards.add(new GUIText(GUI, "(Clear all rewards)\n", RED[0], RED[1], RED[2]).addClickActions(() ->
            {
                rewards.clear();

                rewards.add(new GUIText(GUI, "\n"));
                GUIItemStack rewardElement = new GUIItemStack(GUI, ItemStack.EMPTY);
                rewardElement.text = TextFormatting.DARK_PURPLE + "(Add new reward)";
                rewards.add(rewardElement.addClickActions(() ->
                {
                    ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement);
                    gui.addOnClosedActions(() -> GUI.editReward(rewardElement, gui.selection));
                }));

                rewards.add(new GUIText(GUI, "\n"));
            }));
        }

        rewards.add(new GUIText(GUI, "\n"));


        //Conditions tab
        conditions.clear();

        for (CCondition condition : quest.conditions)
        {
            conditions.add(new GUIText(GUI, "\n"));
            GUICondition conditionElement = new GUICondition(GUI, condition);
            conditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                gui.addOnClosedActions(() -> GUI.editCondition(conditionElement, gui.selection));
            }));
        }

        {
            conditions.add(new GUIText(GUI, "\n"));
            GUICondition conditionElement = new GUICondition(GUI, null);
            conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
            conditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                gui.addOnClosedActions(() -> GUI.editCondition(conditionElement, gui.selection));
            }));
        }

        if (quest.conditions.size() > 0)
        {
            conditions.add(new GUIText(GUI, "\n"));
            conditions.add(new GUIText(GUI, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(() ->
            {
                conditions.clear();

                conditions.add(new GUIText(GUI, "\n"));
                GUICondition conditionElement = new GUICondition(GUI, null);
                conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
                conditions.add(conditionElement.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                    gui.addOnClosedActions(() -> GUI.editCondition(conditionElement, gui.selection));
                }));

                conditions.add(new GUIText(GUI, "\n"));
            }));
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
                spoiler = new GUITextSpoiler(GUI, dialogueEntry.dialogueName.value, WHITE[0], WHITE[1], WHITE[2]);

                dialogues.add(new GUIText(GUI, "\n"));
                dialogues.add(spoiler);
                dialogueSpoilers.put(id, spoiler);

                spoiler.add(new GUIText(GUI, "\n"));
                spoiler.add(new GUIText(GUI, "========================================================================================================================================================================================", WHITE[0]));
                spoiler.add(new GUIText(GUI, "\n"));
                spoiler.add(new GUIText(GUI, "* ", WHITE[0]));
                spoiler.add(new GUIText(GUI, "Branch " + dialogueEntry.branchIndex.value, BLUE[0], BLUE[1], BLUE[2]));
                spoiler.add(new GUIText(GUI, " " + dialogueEntry.relation.value, WHITE[0]));
                spoiler.add(new GUIText(GUI, "\n"));
                spoiler.add(new GUIText(GUI, "========================================================================================================================================================================================", WHITE[0]));
                spoiler.add(new GUIText(GUI, "\n"));
            }
            else
            {
                spoiler.add(spoiler.size() - 2, new GUIText(GUI, "* ", WHITE[0]));
                spoiler.add(spoiler.size() - 2, new GUIText(GUI, "Branch " + dialogueEntry.branchIndex.value, BLUE[0], BLUE[1], BLUE[2]));
                spoiler.add(spoiler.size() - 2, new GUIText(GUI, " " + dialogueEntry.relation.value, WHITE[0]));
                spoiler.add(spoiler.size() - 2, new GUIText(GUI, "\n"));
            }
        }
        dialogues.add(new GUIText(GUI, "\n"));
    }

    @Override
    public void initGui()
    {
        super.initGui();


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
        root.add(cancel.addClickActions(() -> GUI.close()));
        delete = new GUITextButton(this, "Delete Quest and Close", RED[0]);
        root.add(delete);

        separator = new GUIGradientBorder(this, 1, 0.03, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separator);

        tabView = new GUITabView(this, 1, 1 - (separator.y + separator.height), "Main", "Objectives", "Rewards", "Availability Conditions", "Dialogues");
        root.add(tabView);

        main = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(0).add(main);
        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, main));

        objectives = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(1).add(objectives);
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectives));

        rewards = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(2).add(rewards);
        tabView.tabViews.get(2).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, rewards));

        conditions = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(3).add(conditions);
        tabView.tabViews.get(3).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditions));

        dialogues = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(4).add(dialogues);
        tabView.tabViews.get(4).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, dialogues));
    }

    @Override
    public void onClosed()
    {
        super.onClosed();
        Network.WRAPPER.sendToServer(new Network.RequestJournalDataPacket());
    }

    public void editReward(GUIItemStack activeRewardElement, ItemStack newStack)
    {
        if (activeRewardElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new reward)"))
        {
            //Started with empty slot
            if (!newStack.isEmpty())
            {
                //Added new reward
                int index = rewards.indexOf(activeRewardElement);

                {
                    rewards.add(index, new GUIText(GUI, "\n"));
                    GUIItemStack rewardElement = new GUIItemStack(GUI, newStack.copy());
                    rewards.add(index, rewardElement.addClickActions(() ->
                    {
                        ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement);
                        gui.addOnClosedActions(() -> GUI.editReward(rewardElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Rewards were empty, but no longer are
                    rewards.add(new GUIText(this, "(Clear all rewards)\n", RED[0], RED[1], RED[2]).addClickActions(() ->
                    {
                        rewards.clear();

                        rewards.add(new GUIText(this, "\n"));
                        GUIItemStack rewardElement = new GUIItemStack(this, ItemStack.EMPTY);
                        rewardElement.text = TextFormatting.DARK_PURPLE + "(Add new reward)";
                        rewards.add(rewardElement.addClickActions(() ->
                        {
                            ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement);
                            gui.addOnClosedActions(() -> GUI.editReward(rewardElement, gui.selection));
                        }));

                        rewards.add(new GUIText(this, "\n"));
                    }));
                    rewards.add(new GUIText(GUI, "\n"));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (!newStack.isEmpty()) activeRewardElement.setStack(newStack.copy());
            else
            {
                //Removing a reward
                int index = rewards.indexOf(activeRewardElement);
                rewards.remove(index);
                rewards.remove(index);

                if (rewards.size() == 5)
                {
                    //Had one reward, and now have 0 (remove the "clear all" option)
                    rewards.remove(3);
                    rewards.remove(3);
                }
            }
        }
    }

    private void editCondition(GUICondition activeConditionElement, CCondition newCondition)
    {
        if (activeConditionElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
        {
            //Started with empty slot
            if (newCondition != null)
            {
                //Added new condition
                int index = conditions.indexOf(activeConditionElement);

                {
                    conditions.add(index, new GUIText(GUI, "\n"));
                    GUICondition conditionElement = new GUICondition(GUI, (CCondition) newCondition.copy());
                    conditions.add(index, conditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                        gui.addOnClosedActions(() -> GUI.editCondition(conditionElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Conditions were empty, but no longer are
                    conditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(() ->
                    {
                        conditions.clear();

                        conditions.add(new GUIText(this, "\n"));
                        GUICondition conditionElement = new GUICondition(this, null);
                        conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
                        conditions.add(conditionElement.addClickActions(() ->
                        {
                            ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                            gui.addOnClosedActions(() -> GUI.editCondition(conditionElement, gui.selection));
                        }));

                        conditions.add(new GUIText(this, "\n"));
                    }));
                    conditions.add(new GUIText(GUI, "\n"));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newCondition != null) activeConditionElement.setCondition((CCondition) newCondition.copy());
            else
            {
                //Removing a condition
                int index = conditions.indexOf(activeConditionElement);
                conditions.remove(index);
                conditions.remove(index);

                if (conditions.size() == 5)
                {
                    //Had one condition, and now have 0 (remove the "clear all" option)
                    conditions.remove(3);
                    conditions.remove(3);
                }
            }
        }
    }
}

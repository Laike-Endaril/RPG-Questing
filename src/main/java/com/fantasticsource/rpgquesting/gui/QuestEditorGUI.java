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
import com.fantasticsource.rpgquesting.Colors;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.RequestEditorDataPacket;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CRelatedDialogueEntry;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedHashMap;

import static com.fantasticsource.rpgquesting.Colors.*;

public class QuestEditorGUI extends GUIScreen
{
    public static final QuestEditorGUI GUI = new QuestEditorGUI(0.5);

    public GUIScrollView main, objectives, rewards, conditions, dialogues;
    private GUIGradientBorder separator;
    private GUITabView tabView;
    private GUILabeledTextInput name, group, level, repeatable, experience;
    private GUIText oldName;
    private CQuest quest;

    private QuestEditorGUI(double textScale)
    {
        super(textScale);
    }

    public void show(CQuest quest)
    {
        this.quest = quest;


        Minecraft.getMinecraft().displayGuiScreen(this);


        //Main tab
        main.clear();

        main.add(new GUITextSpacer(this));

        name = new GUILabeledTextInput(this, "Name: ", quest.name.value, FilterNotEmpty.INSTANCE);
        main.add(name);
        main.add(new GUITextSpacer(this));

        oldName = new GUIText(this, "(Previous Name: " + quest.name.value + ")", BLUE[0]);
        main.add(oldName);
        main.add(new GUITextSpacer(this));
        main.add(new GUITextSpacer(this));

        group = new GUILabeledTextInput(this, "Group: ", quest.group.value, FilterNotEmpty.INSTANCE);
        main.add(group);
        main.add(new GUITextSpacer(this));

        level = new GUILabeledTextInput(this, "Level: ", "" + quest.level.value, FilterInt.INSTANCE);
        main.add(level);
        main.add(new GUITextSpacer(this));

        repeatable = new GUILabeledTextInput(this, "Repeatable: ", "" + quest.repeatable.value, FilterBoolean.INSTANCE);
        main.add(repeatable);
        main.add(new GUITextSpacer(this));

        experience = new GUILabeledTextInput(this, "Experience Awarded: ", "" + quest.experience.value, FilterInt.INSTANCE);
        main.add(experience);
        main.add(new GUITextSpacer(this));


        //Objectives tab
        objectives.clear();

        for (CObjective objective : quest.objectives)
        {
            objectives.add(new GUITextSpacer(this));
            GUIObjective objectiveElement = new GUIObjective(this, objective);
            objectives.add(objectiveElement.addClickActions(() ->
            {
                ObjectiveEditorGUI gui = new ObjectiveEditorGUI(objectiveElement, textScale);
                gui.addOnClosedActions(() -> editObjective(objectiveElement, gui.selection));
            }));
        }

        {
            objectives.add(new GUITextSpacer(this));
            GUIObjective objectiveElement = new GUIObjective(this, null);
            objectiveElement.text = TextFormatting.DARK_PURPLE + "(Add new objective)";
            objectives.add(objectiveElement.addClickActions(() ->
            {
                ObjectiveEditorGUI gui = new ObjectiveEditorGUI(objectiveElement, textScale);
                gui.addOnClosedActions(() -> editObjective(objectiveElement, gui.selection));
            }));
        }

        if (quest.objectives.size() > 0)
        {
            objectives.add(new GUITextSpacer(this));
            objectives.add(new GUIText(this, "(Clear all objectives)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearObjectives));
        }

        objectives.add(new GUITextSpacer(this));


        //Rewards tab
        rewards.clear();

        for (CItemStack reward : quest.rewards)
        {
            rewards.add(new GUITextSpacer(this));
            GUIItemStack rewardElement = new GUIItemStack(this, reward.value);
            rewards.add(rewardElement.addClickActions(() ->
            {
                ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement, textScale);
                gui.addOnClosedActions(() -> editReward(rewardElement, gui.selection));
            }));
        }

        {
            rewards.add(new GUITextSpacer(this));
            GUIItemStack rewardElement = new GUIItemStack(this, ItemStack.EMPTY);
            rewardElement.text = TextFormatting.DARK_PURPLE + "(Add new reward)";
            rewards.add(rewardElement.addClickActions(() ->
            {
                ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement, textScale);
                gui.addOnClosedActions(() -> editReward(rewardElement, gui.selection));
            }));
        }

        if (quest.rewards.size() > 0)
        {
            rewards.add(new GUITextSpacer(this));
            rewards.add(new GUIText(this, "(Clear all rewards)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearRewards));
        }

        rewards.add(new GUITextSpacer(this));


        //Conditions tab
        conditions.clear();

        for (CCondition condition : quest.conditions)
        {
            conditions.add(new GUITextSpacer(this));
            GUICondition conditionElement = new GUICondition(this, condition);
            conditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
            }));
        }

        {
            conditions.add(new GUITextSpacer(this));
            GUICondition conditionElement = new GUICondition(this, null);
            conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
            conditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
            }));
        }

        if (quest.conditions.size() > 0)
        {
            conditions.add(new GUITextSpacer(this));
            conditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
        }

        conditions.add(new GUITextSpacer(this));


        //Dialogues tab
        dialogues.clear();

        LinkedHashMap<String, GUITextSpoiler> dialogueSpoilers = new LinkedHashMap<>();
        for (CRelatedDialogueEntry dialogueEntry : quest.relatedDialogues)
        {
            String dialogueName = dialogueEntry.dialogueName.value;
            GUITextSpoiler spoiler = dialogueSpoilers.get(dialogueName);
            if (spoiler == null)
            {
                spoiler = new GUITextSpoiler(this, dialogueEntry.dialogueName.value, WHITE[0], WHITE[1], WHITE[2]);

                dialogues.add(new GUITextSpacer(this));
                dialogues.add(spoiler);
                dialogueSpoilers.put(dialogueName, spoiler);

                spoiler.add(new GUITextSpacer(this));
                spoiler.add(new GUIText(this, "========================================================================================================================================================================================", WHITE[0]));
                spoiler.add(new GUITextSpacer(this));
                spoiler.add(new GUIText(this, "* ", WHITE[0]));
                spoiler.add(new GUIText(this, dialogueEntry.getSource(), BLUE[0], BLUE[1], BLUE[2]));
                spoiler.add(new GUIText(this, " " + dialogueEntry.relation.value, WHITE[0]));
                spoiler.add(new GUITextSpacer(this));
                spoiler.add(new GUIText(this, "========================================================================================================================================================================================", WHITE[0]));
                spoiler.add(new GUITextSpacer(this));
            }
            else
            {
                spoiler.add(spoiler.size() - 2, new GUIText(this, "* ", WHITE[0]));
                spoiler.add(spoiler.size() - 2, new GUIText(this, dialogueEntry.getSource(), BLUE[0], BLUE[1], BLUE[2]));
                spoiler.add(spoiler.size() - 2, new GUIText(this, " " + dialogueEntry.relation.value, WHITE[0]));
                spoiler.add(spoiler.size() - 2, new GUITextSpacer(this));
            }
        }
        dialogues.add(new GUITextSpacer(this));
    }


    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));

        //Management
        root.add(new GUITextButton(this, "Save and Close", GREEN[0])).addClickActions(() ->
        {
            if (trySave()) close();
        });
        root.add(new GUITextButton(this, "Close Without Saving").addClickActions(this::close));
        root.add(new GUITextButton(this, "Delete Quest and Close", RED[0]).addClickActions(() ->
        {
            Network.WRAPPER.sendToServer(new Network.RequestDeleteQuestPacket(oldName.text.substring(0, oldName.text.length() - 1).replace("(Previous Name: ", "")));
            close();
        }));
        root.add(new GUITextButton(this, "Duplicate This Quest").addClickActions(() ->
        {
            quest.name.set(quest.name.value + " (copy)");
            MainEditorGUI.duplicateQuest = quest;
            close();
        }));

        //Tabview
        separator = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        tabView = new GUITabView(this, 1, 1 - (separator.y + separator.height), "Main", "Objectives", "Rewards", "Availability Conditions", "Related Dialogues");
        root.add(separator.addRecalcActions(() -> tabView.height = 1 - (separator.y + separator.height)));
        root.add(tabView);

        //Main tab
        {
            GUITextSpacer spacer = new GUITextSpacer(this, true);
            main = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
            tabView.tabViews.get(0).add(spacer.addRecalcActions(() -> main.width = 0.98 - spacer.width * 2));
            tabView.tabViews.get(0).add(main);
            tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, main));
        }

        //Objectives tab
        {
            GUITextSpacer spacer = new GUITextSpacer(this, true);
            objectives = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
            tabView.tabViews.get(1).add(spacer.addRecalcActions(() -> objectives.width = 0.98 - spacer.width * 2));
            tabView.tabViews.get(1).add(objectives);
            tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectives));
        }

        //Rewards tab
        {
            GUITextSpacer spacer = new GUITextSpacer(this, true);
            rewards = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
            tabView.tabViews.get(2).add(spacer.addRecalcActions(() -> rewards.width = 0.98 - spacer.width * 2));
            tabView.tabViews.get(2).add(rewards);
            tabView.tabViews.get(2).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, rewards));
        }

        //Conditions tab
        {
            GUITextSpacer spacer = new GUITextSpacer(this, true);
            conditions = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
            tabView.tabViews.get(3).add(spacer.addRecalcActions(() -> conditions.width = 0.98 - spacer.width * 2));
            tabView.tabViews.get(3).add(conditions);
            tabView.tabViews.get(3).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditions));
        }

        //Dialogues tab
        {
            GUITextSpacer spacer = new GUITextSpacer(this, true);
            dialogues = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
            tabView.tabViews.get(4).add(spacer.addRecalcActions(() -> dialogues.width = 0.98 - spacer.width * 2));
            tabView.tabViews.get(4).add(dialogues);
            tabView.tabViews.get(4).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, dialogues));
        }
    }


    @Override
    public void onClosed()
    {
        super.onClosed();
        Network.WRAPPER.sendToServer(new RequestEditorDataPacket());
    }


    private void editObjective(GUIObjective activeObjectiveElement, CObjective newObjective)
    {
        if (activeObjectiveElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new objective)"))
        {
            //Started with empty slot
            if (newObjective != null)
            {
                //Added new objective
                int index = objectives.indexOf(activeObjectiveElement);

                {
                    objectives.add(index, new GUITextSpacer(this));
                    GUIObjective objectiveElement = new GUIObjective(this, (CObjective) newObjective.copy());
                    objectives.add(index, objectiveElement.addClickActions(() ->
                    {
                        ObjectiveEditorGUI gui = new ObjectiveEditorGUI(objectiveElement, textScale);
                        gui.addOnClosedActions(() -> editObjective(objectiveElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Objectives were empty, but no longer are
                    objectives.add(new GUIText(this, "(Clear all objectives)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearObjectives));
                    objectives.add(new GUITextSpacer(this));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newObjective != null) activeObjectiveElement.setObjective((CObjective) newObjective.copy());
            else
            {
                //Removing an objective
                int index = objectives.indexOf(activeObjectiveElement);
                objectives.remove(index);
                objectives.remove(index);

                if (objectives.size() == 5)
                {
                    //Had one objective, and now have 0 (remove the "clear all" option)
                    objectives.remove(3);
                    objectives.remove(3);
                }
            }
        }
    }

    private void clearObjectives()
    {
        objectives.clear();

        objectives.add(new GUITextSpacer(this));
        GUIObjective objectiveElement = new GUIObjective(this, null);
        objectiveElement.text = TextFormatting.DARK_PURPLE + "(Add new objective)";
        objectives.add(objectiveElement.addClickActions(() ->
        {
            ObjectiveEditorGUI gui = new ObjectiveEditorGUI(objectiveElement, textScale);
            gui.addOnClosedActions(() -> editObjective(objectiveElement, gui.selection));
        }));
        objectives.add(new GUITextSpacer(this));

        tabView.recalc(0);
    }


    private void editReward(GUIItemStack activeRewardElement, ItemStack newStack)
    {
        if (activeRewardElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new reward)"))
        {
            //Started with empty slot
            if (!newStack.isEmpty())
            {
                //Added new reward
                int index = rewards.indexOf(activeRewardElement);

                {
                    rewards.add(index, new GUITextSpacer(this));
                    GUIItemStack rewardElement = new GUIItemStack(this, newStack.copy());
                    rewards.add(index, rewardElement.addClickActions(() ->
                    {
                        ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement, textScale);
                        gui.addOnClosedActions(() -> editReward(rewardElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Rewards were empty, but no longer are
                    rewards.add(new GUIText(this, "(Clear all rewards)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearRewards));
                    rewards.add(new GUITextSpacer(this));
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

    private void clearRewards()
    {
        rewards.clear();

        rewards.add(new GUITextSpacer(this));
        GUIItemStack rewardElement = new GUIItemStack(this, ItemStack.EMPTY);
        rewardElement.text = TextFormatting.DARK_PURPLE + "(Add new reward)";
        rewards.add(rewardElement.addClickActions(() ->
        {
            ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement, textScale);
            gui.addOnClosedActions(() -> editReward(rewardElement, gui.selection));
        }));
        rewards.add(new GUITextSpacer(this));

        tabView.recalc(0);
    }


    private void editCondition(GUICondition activeObjectiveElement, CCondition newCondition)
    {
        if (activeObjectiveElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
        {
            //Started with empty slot
            if (newCondition != null)
            {
                //Added new condition
                int index = conditions.indexOf(activeObjectiveElement);

                {
                    conditions.add(index, new GUITextSpacer(this));
                    GUICondition conditionElement = new GUICondition(this, (CCondition) newCondition.copy());
                    conditions.add(index, conditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                        gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Conditions were empty, but no longer are
                    conditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
                    conditions.add(new GUITextSpacer(this));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newCondition != null) activeObjectiveElement.setCondition((CCondition) newCondition.copy());
            else
            {
                //Removing a condition
                int index = conditions.indexOf(activeObjectiveElement);
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

    private void clearConditions()
    {
        conditions.clear();

        conditions.add(new GUITextSpacer(this));
        GUICondition conditionElement = new GUICondition(this, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
        conditions.add(conditionElement.addClickActions(() ->
        {
            ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
            gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
        }));
        conditions.add(new GUITextSpacer(this));

        tabView.recalc(0);
    }

    private boolean trySave()
    {
        //TODO Add error messages here?
        if (!name.input.valid() || !group.input.valid() || !level.input.valid() || !repeatable.input.valid() || !experience.input.valid()) return false;


        CQuest quest = new CQuest(name.input.text, group.input.text, FilterInt.INSTANCE.parse(level.input.text), FilterBoolean.INSTANCE.parse(repeatable.input.text));
        quest.setExp(FilterInt.INSTANCE.parse(experience.input.text));

        for (GUIElement element : objectives.children)
        {
            if (!(element instanceof GUIObjective)) continue;

            CObjective objective = ((GUIObjective) element).objective;
            if (objective == null) continue;

            quest.addObjectives(objective);
        }

        for (GUIElement element : rewards.children)
        {
            if (!(element instanceof GUIItemStack)) continue;

            ItemStack stack = ((GUIItemStack) element).getStack();
            if (stack == null || stack.isEmpty()) continue;

            quest.addRewards(stack);
        }

        for (GUIElement element : conditions.children)
        {
            if (!(element instanceof GUICondition)) continue;

            CCondition condition = ((GUICondition) element).condition;
            if (condition == null) continue;

            quest.addConditions(condition);
        }

        Network.WRAPPER.sendToServer(new Network.RequestSaveQuestPacket(quest, this.quest.name.value));

        return true;
    }
}

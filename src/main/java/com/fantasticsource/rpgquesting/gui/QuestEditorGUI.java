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
    public static final QuestEditorGUI GUI = new QuestEditorGUI();

    public GUIScrollView main, objectives, rewards, conditions, dialogues;
    private GUIGradientBorder separator;
    private GUITabView tabView;
    private GUILabeledTextInput name, group, level, repeatable, experience;
    private GUIText oldName;

    public void show(CQuest quest)
    {
        Minecraft.getMinecraft().displayGuiScreen(this);


        //Main tab
        main.clear();

        main.add(new GUIText(this, "\n"));

        name = new GUILabeledTextInput(this, "Name: ", quest.name.value, FilterNotEmpty.INSTANCE);
        main.add(name);
        main.add(new GUIText(this, "\n"));

        oldName = new GUIText(this, "(Previous Name: " + quest.name.value + ")", BLUE[0]);
        main.add(oldName);
        main.add(new GUIText(this, "\n\n"));

        group = new GUILabeledTextInput(this, "Group: ", quest.group.value, FilterNotEmpty.INSTANCE);
        main.add(group);
        main.add(new GUIText(this, "\n"));

        level = new GUILabeledTextInput(this, "Level: ", "" + quest.level.value, FilterInt.INSTANCE);
        main.add(level);
        main.add(new GUIText(this, "\n"));

        repeatable = new GUILabeledTextInput(this, "Repeatable: ", "" + quest.repeatable.value, FilterBoolean.INSTANCE);
        main.add(repeatable);
        main.add(new GUIText(this, "\n"));

        experience = new GUILabeledTextInput(this, "Experience Awarded: ", "" + quest.experience.value, FilterInt.INSTANCE);
        main.add(experience);
        main.add(new GUIText(this, "\n"));


        //Objectives tab
        objectives.clear();

        for (CObjective objective : quest.objectives)
        {
            objectives.add(new GUIText(this, "\n"));
            GUIObjective objectiveElement = new GUIObjective(this, objective);
            objectives.add(objectiveElement.addClickActions(() ->
            {
                ObjectiveEditorGUI gui = new ObjectiveEditorGUI(objectiveElement);
                gui.addOnClosedActions(() -> editObjective(objectiveElement, gui.selection));
            }));
        }

        {
            objectives.add(new GUIText(this, "\n"));
            GUIObjective objectiveElement = new GUIObjective(this, null);
            objectiveElement.text = TextFormatting.DARK_PURPLE + "(Add new objective)";
            objectives.add(objectiveElement.addClickActions(() ->
            {
                ObjectiveEditorGUI gui = new ObjectiveEditorGUI(objectiveElement);
                gui.addOnClosedActions(() -> editObjective(objectiveElement, gui.selection));
            }));
        }

        if (quest.objectives.size() > 0)
        {
            objectives.add(new GUIText(this, "\n"));
            objectives.add(new GUIText(this, "(Clear all objectives)\n", RED[0], RED[1], RED[2]).addClickActions(() ->
            {
                objectives.clear();

                objectives.add(new GUIText(this, "\n"));
                GUIObjective objectiveElement = new GUIObjective(this, null);
                objectiveElement.text = TextFormatting.DARK_PURPLE + "(Add new objective)";
                objectives.add(objectiveElement.addClickActions(() ->
                {
                    ObjectiveEditorGUI gui = new ObjectiveEditorGUI(objectiveElement);
                    gui.addOnClosedActions(() -> editObjective(objectiveElement, gui.selection));
                }));

                objectives.add(new GUIText(this, "\n"));
            }));
        }

        objectives.add(new GUIText(this, "\n"));


        //Rewards tab
        rewards.clear();

        for (CItemStack reward : quest.rewards)
        {
            rewards.add(new GUIText(this, "\n"));
            GUIItemStack rewardElement = new GUIItemStack(this, reward.stack);
            rewards.add(rewardElement.addClickActions(() ->
            {
                ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement);
                gui.addOnClosedActions(() -> editReward(rewardElement, gui.selection));
            }));
        }

        {
            rewards.add(new GUIText(this, "\n"));
            GUIItemStack rewardElement = new GUIItemStack(this, ItemStack.EMPTY);
            rewardElement.text = TextFormatting.DARK_PURPLE + "(Add new reward)";
            rewards.add(rewardElement.addClickActions(() ->
            {
                ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement);
                gui.addOnClosedActions(() -> editReward(rewardElement, gui.selection));
            }));
        }

        if (quest.rewards.size() > 0)
        {
            rewards.add(new GUIText(this, "\n"));
            rewards.add(new GUIText(this, "(Clear all rewards)\n", RED[0], RED[1], RED[2]).addClickActions(() ->
            {
                rewards.clear();

                rewards.add(new GUIText(this, "\n"));
                GUIItemStack rewardElement = new GUIItemStack(this, ItemStack.EMPTY);
                rewardElement.text = TextFormatting.DARK_PURPLE + "(Add new reward)";
                rewards.add(rewardElement.addClickActions(() ->
                {
                    ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement);
                    gui.addOnClosedActions(() -> editReward(rewardElement, gui.selection));
                }));

                rewards.add(new GUIText(this, "\n"));
            }));
        }

        rewards.add(new GUIText(this, "\n"));


        //Conditions tab
        conditions.clear();

        for (CCondition condition : quest.conditions)
        {
            conditions.add(new GUIText(this, "\n"));
            GUICondition conditionElement = new GUICondition(this, condition);
            conditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
            }));
        }

        {
            conditions.add(new GUIText(this, "\n"));
            GUICondition conditionElement = new GUICondition(this, null);
            conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
            conditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
            }));
        }

        if (quest.conditions.size() > 0)
        {
            conditions.add(new GUIText(this, "\n"));
            conditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(() ->
            {
                conditions.clear();

                conditions.add(new GUIText(this, "\n"));
                GUICondition conditionElement = new GUICondition(this, null);
                conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
                conditions.add(conditionElement.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                    gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
                }));

                conditions.add(new GUIText(this, "\n"));
            }));
        }

        conditions.add(new GUIText(this, "\n"));


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

                dialogues.add(new GUIText(this, "\n"));
                dialogues.add(spoiler);
                dialogueSpoilers.put(dialogueName, spoiler);

                spoiler.add(new GUIText(this, "\n"));
                spoiler.add(new GUIText(this, "========================================================================================================================================================================================", WHITE[0]));
                spoiler.add(new GUIText(this, "\n"));
                spoiler.add(new GUIText(this, "* ", WHITE[0]));
                spoiler.add(new GUIText(this, "Branch " + dialogueEntry.branchIndex.value, BLUE[0], BLUE[1], BLUE[2]));
                spoiler.add(new GUIText(this, " " + dialogueEntry.relation.value, WHITE[0]));
                spoiler.add(new GUIText(this, "\n"));
                spoiler.add(new GUIText(this, "========================================================================================================================================================================================", WHITE[0]));
                spoiler.add(new GUIText(this, "\n"));
            }
            else
            {
                spoiler.add(spoiler.size() - 2, new GUIText(this, "* ", WHITE[0]));
                spoiler.add(spoiler.size() - 2, new GUIText(this, "Branch " + dialogueEntry.branchIndex.value, BLUE[0], BLUE[1], BLUE[2]));
                spoiler.add(spoiler.size() - 2, new GUIText(this, " " + dialogueEntry.relation.value, WHITE[0]));
                spoiler.add(spoiler.size() - 2, new GUIText(this, "\n"));
            }
        }
        dialogues.add(new GUIText(this, "\n"));
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
        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        //Management
        root.add(new GUITextButton(this, "Save", GREEN[0])).addClickActions(this::trySave);
        root.add(new GUITextButton(this, "Close Editor").addClickActions(this::close));
        root.add(new GUITextButton(this, "Delete Quest", RED[0]).addClickActions(() -> Network.WRAPPER.sendToServer(new Network.RequestDeleteQuestPacket(oldName.text.substring(0, oldName.text.length() - 1).replace("(Previous Name: ", "")))));

        //Tabview
        separator = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separator);
        tabView = new GUITabView(this, 1, 1 - (separator.y + separator.height), "Main", "Objectives", "Rewards", "Availability Conditions", "Dialogues");
        root.add(tabView);

        //Main tab
        main = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(0).add(main);
        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, main));

        //Objectives tab
        objectives = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(1).add(objectives);
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectives));

        //Rewards tab
        rewards = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(2).add(rewards);
        tabView.tabViews.get(2).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, rewards));

        //Conditions tab
        conditions = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(3).add(conditions);
        tabView.tabViews.get(3).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditions));

        //Dialogues tab
        dialogues = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(4).add(dialogues);
        tabView.tabViews.get(4).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, dialogues));
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
                    objectives.add(index, new GUIText(this, "\n"));
                    GUIObjective objectiveElement = new GUIObjective(this, (CObjective) newObjective.copy());
                    objectives.add(index, objectiveElement.addClickActions(() ->
                    {
                        ObjectiveEditorGUI gui = new ObjectiveEditorGUI(objectiveElement);
                        gui.addOnClosedActions(() -> editObjective(objectiveElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Objectives were empty, but no longer are
                    objectives.add(new GUIText(this, "(Clear all objectives)\n", RED[0], RED[1], RED[2]).addClickActions(() ->
                    {
                        objectives.clear();

                        objectives.add(new GUIText(this, "\n"));
                        GUIObjective objectiveElement = new GUIObjective(this, null);
                        objectiveElement.text = TextFormatting.DARK_PURPLE + "(Add new objective)";
                        objectives.add(objectiveElement.addClickActions(() ->
                        {
                            ObjectiveEditorGUI gui = new ObjectiveEditorGUI(objectiveElement);
                            gui.addOnClosedActions(() -> editObjective(objectiveElement, gui.selection));
                        }));

                        objectives.add(new GUIText(this, "\n"));
                    }));
                    objectives.add(new GUIText(this, "\n"));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newObjective != null) activeObjectiveElement.setObjective((CObjective) newObjective.copy());
            else
            {
                //Removing a objective
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
                    rewards.add(index, new GUIText(this, "\n"));
                    GUIItemStack rewardElement = new GUIItemStack(this, newStack.copy());
                    rewards.add(index, rewardElement.addClickActions(() ->
                    {
                        ItemSelectionGUI gui = new ItemSelectionGUI(rewardElement);
                        gui.addOnClosedActions(() -> editReward(rewardElement, gui.selection));
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
                            gui.addOnClosedActions(() -> editReward(rewardElement, gui.selection));
                        }));

                        rewards.add(new GUIText(this, "\n"));
                    }));
                    rewards.add(new GUIText(this, "\n"));
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
                //Added new objective
                int index = conditions.indexOf(activeConditionElement);

                {
                    conditions.add(index, new GUIText(this, "\n"));
                    GUICondition conditionElement = new GUICondition(this, (CCondition) newCondition.copy());
                    conditions.add(index, conditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                        gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
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
                            gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
                        }));

                        conditions.add(new GUIText(this, "\n"));
                    }));
                    conditions.add(new GUIText(this, "\n"));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newCondition != null) activeConditionElement.setCondition((CCondition) newCondition.copy());
            else
            {
                //Removing a objective
                int index = conditions.indexOf(activeConditionElement);
                conditions.remove(index);
                conditions.remove(index);

                if (conditions.size() == 5)
                {
                    //Had one objective, and now have 0 (remove the "clear all" option)
                    conditions.remove(3);
                    conditions.remove(3);
                }
            }
        }
    }

    boolean trySave()
    {
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

        Network.WRAPPER.sendToServer(new Network.RequestSaveQuestPacket(quest));
        return true;
    }
}

package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Colors;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.actions.CActionBranch;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogueChoice;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.Colors.*;

public class DialogueEditorGUI extends GUIScreen
{
    public static final DialogueEditorGUI GUI = new DialogueEditorGUI(0.5);

    public GUIScrollView main, playerConditions, entityConditions, branches;
    private GUIGradientBorder separator;
    private GUITabView tabView;
    private GUILabeledTextInput name, group;
    private GUIText oldName;

    private DialogueEditorGUI(double textScale)
    {
        super(textScale);
    }

    public void show(CDialogue dialogue)
    {
        Minecraft.getMinecraft().displayGuiScreen(this);


        //Main tab
        main.clear();

        main.add(new GUITextSpacer(this));

        name = new GUILabeledTextInput(this, "Name: ", dialogue.name.value, FilterNotEmpty.INSTANCE);
        main.add(name.addRecalcActions(() ->
        {
            //For CActionBranch, way down in DialogueEditorGUI -> BranchEditorGUI -> ChoiceEditorGUI -> ActionEditorGUI
            if (name.input.valid()) CActionBranch.queuedDialogueName = name.input.text;
        }));
        main.add(new GUITextSpacer(this));

        oldName = new GUIText(this, "(Previous Name: " + dialogue.name.value + ")", BLUE[0]);
        main.add(oldName);
        main.add(new GUITextSpacer(this));
        main.add(new GUITextSpacer(this));

        group = new GUILabeledTextInput(this, "Group: ", dialogue.group.value, FilterNotEmpty.INSTANCE);
        main.add(group);
        main.add(new GUITextSpacer(this));


        //Availability conditions (player) tab
        playerConditions.clear();

        for (CCondition condition : dialogue.playerConditions)
        {
            playerConditions.add(new GUITextSpacer(this));
            GUICondition conditionElement = new GUICondition(this, condition);
            playerConditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                gui.addOnClosedActions(() -> editPlayerCondition(conditionElement, gui.selection));
            }));
        }

        {
            playerConditions.add(new GUITextSpacer(this));
            GUICondition conditionElement = new GUICondition(this, null);
            conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
            playerConditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                gui.addOnClosedActions(() -> editPlayerCondition(conditionElement, gui.selection));
            }));
        }

        if (dialogue.playerConditions.size() > 0)
        {
            playerConditions.add(new GUITextSpacer(this));
            playerConditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearPlayerConditions));
        }

        playerConditions.add(new GUITextSpacer(this));


        //Availability conditions (entity) tab
        entityConditions.clear();

        for (CCondition condition : dialogue.entityConditions)
        {
            entityConditions.add(new GUITextSpacer(this));
            GUICondition conditionElement = new GUICondition(this, condition);
            entityConditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                gui.addOnClosedActions(() -> editEntityCondition(conditionElement, gui.selection));
            }));
        }

        {
            entityConditions.add(new GUITextSpacer(this));
            GUICondition conditionElement = new GUICondition(this, null);
            conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
            entityConditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                gui.addOnClosedActions(() -> editEntityCondition(conditionElement, gui.selection));
            }));
        }

        if (dialogue.entityConditions.size() > 0)
        {
            entityConditions.add(new GUITextSpacer(this));
            entityConditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearEntityConditions));
        }

        entityConditions.add(new GUITextSpacer(this));


        //Branches tab
        branches.clear();

        int i = 0;
        for (CDialogueBranch branch : dialogue.branches)
        {
            branches.add(new GUITextSpacer(this));
            GUIBranch branchElement = new GUIBranch(this, branch, "Branch " + i++);
            branches.add(branchElement.addClickActions(() ->
            {
                BranchEditorGUI gui = new BranchEditorGUI(branchElement, textScale);
                gui.addOnClosedActions(() -> editBranch(branchElement, gui.selection));
            }));
        }

        {
            branches.add(new GUITextSpacer(this));
            GUIBranch branchElement = new GUIBranch(this, null, TextFormatting.DARK_PURPLE + "(Add new branch)");
            branches.add(branchElement.addClickActions(() ->
            {
                BranchEditorGUI gui = new BranchEditorGUI(branchElement, textScale);
                gui.addOnClosedActions(() -> editBranch(branchElement, gui.selection));
            }));
        }

        if (dialogue.branches.size() > 0)
        {
            branches.add(new GUITextSpacer(this));
            branches.add(new GUIText(this, "(Clear all branches)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearBranches));
        }

        branches.add(new GUITextSpacer(this));
    }

    @Override
    public void onClosed()
    {
        super.onClosed();
        Network.WRAPPER.sendToServer(new Network.RequestEditorDataPacket());
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));

        //Management
        root.add(new GUITextButton(this, "Save", GREEN[0])).addClickActions(this::trySave);
        root.add(new GUITextButton(this, "Close Editor").addClickActions(this::close));
        root.add(new GUITextButton(this, "Delete Dialogue", RED[0]).addClickActions(() -> Network.WRAPPER.sendToServer(new Network.RequestDeleteDialoguePacket(oldName.text.substring(0, oldName.text.length() - 1).replace("(Previous Name: ", "")))));

        //Tabview
        separator = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        tabView = new GUITabView(this, 1, 1 - (separator.y + separator.height), "Main", "Availability Conditions (Player)", "Availability Conditions (Entity)", "Branches");
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

        //Availability conditions (player) tab
        {
            GUITextSpacer spacer = new GUITextSpacer(this, true);
            playerConditions = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
            tabView.tabViews.get(1).add(spacer.addRecalcActions(() -> playerConditions.width = 0.98 - spacer.width * 2));
            tabView.tabViews.get(1).add(playerConditions);
            tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, playerConditions));
        }

        //Availability conditions (entity) tab
        {
            GUITextSpacer spacer = new GUITextSpacer(this, true);
            entityConditions = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
            tabView.tabViews.get(2).add(spacer.addRecalcActions(() -> entityConditions.width = 0.98 - spacer.width * 2));
            tabView.tabViews.get(2).add(entityConditions);
            tabView.tabViews.get(2).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, entityConditions));
        }

        //Branches tab
        {
            GUITextSpacer spacer = new GUITextSpacer(this, true);
            branches = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
            tabView.tabViews.get(3).add(spacer.addRecalcActions(() -> branches.width = 0.98 - spacer.width * 2));
            tabView.tabViews.get(3).add(branches);
            tabView.tabViews.get(3).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, branches));
        }
    }


    private void trySave()
    {
        //TODO Add error messages here?
        if (!name.input.valid() || !group.input.valid()) return;


        CDialogue dialogue = new CDialogue(name.input.text, group.input.text);

        for (GUIElement element : playerConditions.children)
        {
            if (!(element instanceof GUICondition)) continue;

            CCondition condition = ((GUICondition) element).condition;
            if (condition == null) continue;

            condition.setDialogueData(dialogue.name.value, 0);

            dialogue.addPlayerConditions(condition);
        }

        for (GUIElement element : entityConditions.children)
        {
            if (!(element instanceof GUICondition)) continue;

            CCondition condition = ((GUICondition) element).condition;
            if (condition == null) continue;

            condition.setDialogueData(dialogue.name.value, 0);

            dialogue.addEntityConditions(condition);
        }

        for (GUIElement element : branches.children)
        {
            if (!(element instanceof GUIBranch)) continue;

            CDialogueBranch branch = ((GUIBranch) element).branch;
            if (branch == null) continue;

            branch.dialogueName.set(name.input.text);

            int index = 0;
            for (CDialogueChoice choice : branch.choices)
            {
                for (CCondition condition : choice.availabilityConditions)
                {
                    condition.setDialogueData(dialogue.name.value, index);
                }

                choice.action.setDialogueName(dialogue.name.value);

                index++;
            }

            dialogue.branches.add(branch);
        }

        Network.WRAPPER.sendToServer(new Network.RequestSaveDialoguePacket(dialogue));
    }


    private void editPlayerCondition(GUICondition activeConditionElement, CCondition newCondition)
    {
        if (activeConditionElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
        {
            //Started with empty slot
            if (newCondition != null)
            {
                //Added new condition
                int index = playerConditions.indexOf(activeConditionElement);

                {
                    playerConditions.add(index, new GUITextSpacer(this));
                    GUICondition conditionElement = new GUICondition(this, (CCondition) newCondition.copy());
                    playerConditions.add(index, conditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                        gui.addOnClosedActions(() -> editPlayerCondition(conditionElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Conditions were empty, but no longer are
                    playerConditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearPlayerConditions));
                    playerConditions.add(new GUITextSpacer(this));
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
                int index = playerConditions.indexOf(activeConditionElement);
                playerConditions.remove(index);
                playerConditions.remove(index);

                if (playerConditions.size() == 5)
                {
                    //Had one condition, and now have 0 (remove the "clear all" option)
                    playerConditions.remove(3);
                    playerConditions.remove(3);
                }
            }
        }

        tabView.recalc(0);
    }

    private void clearPlayerConditions()
    {
        playerConditions.clear();

        playerConditions.add(new GUITextSpacer(this));
        GUICondition conditionElement = new GUICondition(this, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
        playerConditions.add(conditionElement.addClickActions(() ->
        {
            ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
            gui.addOnClosedActions(() -> editPlayerCondition(conditionElement, gui.selection));
        }));
        playerConditions.add(new GUITextSpacer(this));

        tabView.recalc(0);
    }


    private void editEntityCondition(GUICondition activeConditionElement, CCondition newCondition)
    {
        if (activeConditionElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
        {
            //Started with empty slot
            if (newCondition != null)
            {
                //Added new condition
                int index = entityConditions.indexOf(activeConditionElement);

                {
                    entityConditions.add(index, new GUITextSpacer(this));
                    GUICondition conditionElement = new GUICondition(this, (CCondition) newCondition.copy());
                    entityConditions.add(index, conditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                        gui.addOnClosedActions(() -> editEntityCondition(conditionElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Conditions were empty, but no longer are
                    entityConditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearEntityConditions));
                    entityConditions.add(new GUITextSpacer(this));
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
                int index = entityConditions.indexOf(activeConditionElement);
                entityConditions.remove(index);
                entityConditions.remove(index);

                if (entityConditions.size() == 5)
                {
                    //Had one condition, and now have 0 (remove the "clear all" option)
                    entityConditions.remove(3);
                    entityConditions.remove(3);
                }
            }
        }

        tabView.recalc(0);
    }

    private void clearEntityConditions()
    {
        entityConditions.clear();

        entityConditions.add(new GUITextSpacer(this));
        GUICondition conditionElement = new GUICondition(this, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
        entityConditions.add(conditionElement.addClickActions(() ->
        {
            ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
            gui.addOnClosedActions(() -> editEntityCondition(conditionElement, gui.selection));
        }));
        entityConditions.add(new GUITextSpacer(this));

        tabView.recalc(0);
    }


    private void editBranch(GUIBranch activeBranchElement, CDialogueBranch newBranch)
    {
        if (activeBranchElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new branch)"))
        {
            //Started with empty slot
            if (newBranch != null)
            {
                //Added new branch
                int index = branches.indexOf(activeBranchElement);

                {
                    branches.add(index, new GUITextSpacer(this));
                    GUIBranch branchElement = new GUIBranch(this, (CDialogueBranch) newBranch.copy(), "Branch " + (index / 2));
                    branches.add(index, branchElement.addClickActions(() ->
                    {
                        BranchEditorGUI gui = new BranchEditorGUI(branchElement, textScale);
                        gui.addOnClosedActions(() -> editBranch(branchElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Branches were empty, but no longer are
                    branches.add(new GUIText(this, "(Clear all branches)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearBranches));
                    branches.add(new GUITextSpacer(this));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newBranch != null) activeBranchElement.setBranch((CDialogueBranch) newBranch.copy());
            else
            {
                //Removing a branch
                int index = branches.indexOf(activeBranchElement);
                branches.remove(index);
                branches.remove(index);

                if (branches.size() == 5)
                {
                    //Had one branch, and now have 0 (remove the "clear all" option)
                    branches.remove(3);
                    branches.remove(3);
                }
            }
        }

        tabView.recalc(0);
    }

    private void clearBranches()
    {
        branches.clear();

        branches.add(new GUITextSpacer(this));
        GUIBranch branchElement = new GUIBranch(this, null, TextFormatting.DARK_PURPLE + "(Add new branch)");
        branches.add(branchElement.addClickActions(() ->
        {
            BranchEditorGUI gui = new BranchEditorGUI(branchElement, textScale);
            gui.addOnClosedActions(() -> editBranch(branchElement, gui.selection));
        }));
        branches.add(new GUITextSpacer(this));

        tabView.recalc(0);
    }
}

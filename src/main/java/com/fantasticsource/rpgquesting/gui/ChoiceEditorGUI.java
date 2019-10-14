package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.actions.CAction;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogueChoice;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class ChoiceEditorGUI extends GUIScreen
{
    public CDialogueChoice selection;
    private GUITabView tabView;
    private GUIScrollView conditionsView, actionsView;
    private GUIGradientBorder separator;
    private GUILabeledTextInput text;

    public ChoiceEditorGUI(GUIChoice clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        selection = clickedElement.choice;


        //Main tab
        tabView.tabViews.get(0).clear();
        tabView.tabViews.get(0).add(new GUIText(this, "\n"));
        text = new GUILabeledTextInput(this, " Text: ", selection.text.value, FilterNotEmpty.INSTANCE);
        tabView.tabViews.get(0).add(text);


        //Conditions tab
        conditionsView.clear();

        for (CCondition condition : selection.availabilityConditions)
        {
            conditionsView.add(new GUIText(this, "\n"));
            GUICondition conditionElement = new GUICondition(this, condition);
            conditionsView.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
            }));
        }

        {
            conditionsView.add(new GUIText(this, "\n"));
            GUICondition conditionElement = new GUICondition(this, null);
            conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
            conditionsView.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
            }));
        }

        if (selection.availabilityConditions.size() > 0)
        {
            conditionsView.add(new GUIText(this, "\n"));
            conditionsView.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
        }

        conditionsView.add(new GUIText(this, "\n"));


        //Actions tab
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);


        //Resize views and scrollbars
        tabView.y = separator.y + separator.height;
        tabView.height = 1 - tabView.y;


        root.recalc();
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));


        //Management
        GUITextButton save = new GUITextButton(this, "Save and Close", GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            //TODO create new choice and set selection to it
            close();
        }));

        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel.addClickActions(this::close));

        GUITextButton delete = new GUITextButton(this, "Delete Choice and Close", RED[0]);
        root.add(delete.addClickActions(() ->
        {
            selection = null;
            close();
        }));


        separator = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separator);


        tabView = new GUITabView(this, 0, separator.y + separator.height, 1, 1 - separator.y - separator.height, "Main", "Availability Conditions", "Actions");
        root.add(tabView);


        //Conditions tab
        conditionsView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(0).add(conditionsView);
        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionsView));


        //Actions tab
        actionsView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(1).add(actionsView);
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, actionsView));
    }


    private void editCondition(GUICondition activeObjectiveElement, CCondition newCondition)
    {
        if (activeObjectiveElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
        {
            //Started with empty slot
            if (newCondition != null)
            {
                //Added new condition
                int index = conditionsView.indexOf(activeObjectiveElement);

                {
                    conditionsView.add(index, new GUIText(this, "\n"));
                    GUICondition conditionElement = new GUICondition(this, (CCondition) newCondition.copy());
                    conditionsView.add(index, conditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                        gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Objectives were empty, but no longer are
                    conditionsView.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
                    conditionsView.add(new GUIText(this, "\n"));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newCondition != null) activeObjectiveElement.setCondition((CCondition) newCondition.copy());
            else
            {
                //Removing an objective
                int index = conditionsView.indexOf(activeObjectiveElement);
                conditionsView.remove(index);
                conditionsView.remove(index);

                if (conditionsView.size() == 5)
                {
                    //Had one objective, and now have 0 (remove the "clear all" option)
                    conditionsView.remove(3);
                    conditionsView.remove(3);
                }
            }
        }
    }

    private void clearConditions()
    {
        conditionsView.clear();

        conditionsView.add(new GUIText(this, "\n"));
        GUICondition conditionElement = new GUICondition(this, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
        conditionsView.add(conditionElement.addClickActions(() ->
        {
            ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
            gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
        }));
        conditionsView.add(new GUIText(this, "\n"));

        tabView.recalc();
    }


    private void editAction(GUIAction activeObjectiveElement, CAction newAction)
    {
        if (activeObjectiveElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new action)"))
        {
            //Started with empty slot
            if (newAction != null)
            {
                //Added new action
                int index = actionsView.indexOf(activeObjectiveElement);

                {
                    actionsView.add(index, new GUIText(this, "\n"));
                    GUIAction actionElement = new GUIAction(this, (CAction) newAction.copy());
                    actionsView.add(index, actionElement.addClickActions(() ->
                    {
                        ActionEditorGUI gui = new ActionEditorGUI(actionElement);
                        gui.addOnClosedActions(() -> editAction(actionElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Objectives were empty, but no longer are
                    actionsView.add(new GUIText(this, "(Clear all actions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearActions));
                    actionsView.add(new GUIText(this, "\n"));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newAction != null) activeObjectiveElement.setAction((CAction) newAction.copy());
            else
            {
                //Removing an objective
                int index = actionsView.indexOf(activeObjectiveElement);
                actionsView.remove(index);
                actionsView.remove(index);

                if (actionsView.size() == 5)
                {
                    //Had one objective, and now have 0 (remove the "clear all" option)
                    actionsView.remove(3);
                    actionsView.remove(3);
                }
            }
        }
    }

    private void clearActions()
    {
        actionsView.clear();

        actionsView.add(new GUIText(this, "\n"));
        GUIAction actionElement = new GUIAction(this, null);
        actionElement.text = TextFormatting.DARK_PURPLE + "(Add new action)";
        actionsView.add(actionElement.addClickActions(() ->
        {
            ActionEditorGUI gui = new ActionEditorGUI(actionElement);
            gui.addOnClosedActions(() -> editAction(actionElement, gui.selection));
        }));
        actionsView.add(new GUIText(this, "\n"));

        tabView.recalc();
    }
}

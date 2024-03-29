package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Colors;
import com.fantasticsource.rpgquesting.actions.CActionArray;
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
    private GUIScrollView mainView, conditionsView;
    private GUIGradientBorder separator;
    private GUILabeledTextInput text;
    private GUIAction action;

    public ChoiceEditorGUI(GUIChoice clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        selection = clickedElement.choice;


        //Main tab
        mainView.clear();
        mainView.add(new GUITextSpacer(this));
        text = new GUILabeledTextInput(this, "Text: ", selection == null ? "" : selection.text.value, FilterNotEmpty.INSTANCE);
        mainView.add(text);

        mainView.add(new GUITextSpacer(this));
        mainView.add(new GUITextSpacer(this));
        mainView.add(new GUIText(this, "Action...\n"));
        mainView.add(new GUITextSpacer(this));
        action = selection == null ? new GUIAction(this, new CActionArray()) : new GUIAction(this, selection.action);
        mainView.add(action.addClickActions(() ->
        {
            ActionEditorGUI gui = new ActionEditorGUI(action, textScale);
            gui.addOnClosedActions(() ->
            {
                action.setAction(gui.selection);
                mainView.recalc(0);
            });
        }));

        mainView.add(new GUITextSpacer(this));


        //Conditions tab
        conditionsView.clear();

        if (selection != null)
        {
            for (CCondition condition : selection.availabilityConditions)
            {
                conditionsView.add(new GUITextSpacer(this));
                GUICondition conditionElement = new GUICondition(this, condition);
                conditionsView.add(conditionElement.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                    gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
                }));
            }
        }

        {
            conditionsView.add(new GUITextSpacer(this));
            GUICondition conditionElement = new GUICondition(this, null);
            conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
            conditionsView.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
            }));
        }

        if (selection != null && selection.availabilityConditions.size() > 0)
        {
            conditionsView.add(new GUITextSpacer(this));
            conditionsView.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
        }

        conditionsView.add(new GUITextSpacer(this));
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);


        //Resize views and scrollbars
        tabView.y = separator.y + separator.height;
        tabView.height = 1 - tabView.y;


        root.recalc(0);
    }

    @Override
    public String title()
    {
        return "Choice";
    }

    @Override
    protected void init()
    {
        //Background
        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));


        //Navbar
        root.add(new GUINavbar(this, Color.AQUA));


        //Management
        GUITextButton save = new GUITextButton(this, "Save and Close", GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            //TODO Add error messages here?
            if (!text.input.valid()) return;


            selection = new CDialogueChoice(text.input.text);

            selection.setAction(action.action);

            selection.availabilityConditions.clear();
            for (GUIElement element : conditionsView.children)
            {
                if (element instanceof GUICondition)
                {
                    CCondition condition = ((GUICondition) element).condition;
                    if (condition != null) selection.availabilityConditions.add(condition);
                }
            }

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


        tabView = new GUITabView(this, 0, separator.y + separator.height, 1, 1 - separator.y - separator.height, "Main", "Availability Conditions");
        root.add(tabView);


        //Main tab
        GUITextSpacer spacer = new GUITextSpacer(this, true);
        mainView = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
        tabView.tabViews.get(0).add(spacer.addRecalcActions(() -> mainView.width = 0.98 - spacer.width * 2));
        tabView.tabViews.get(0).add(mainView);

        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, mainView));


        //Conditions tab
        GUITextSpacer spacer2 = new GUITextSpacer(this, true);
        conditionsView = new GUIScrollView(this, 0.98 - spacer2.width * 2, 1);
        tabView.tabViews.get(1).add(spacer2.addRecalcActions(() -> conditionsView.width = 0.98 - spacer2.width * 2));
        tabView.tabViews.get(1).add(conditionsView);

        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionsView));
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
                    conditionsView.add(index, new GUITextSpacer(this));
                    GUICondition conditionElement = new GUICondition(this, (CCondition) newCondition.copy());
                    conditionsView.add(index, conditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                        gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Objectives were empty, but no longer are
                    conditionsView.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
                    conditionsView.add(new GUITextSpacer(this));
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

        conditionsView.add(new GUITextSpacer(this));
        GUICondition conditionElement = new GUICondition(this, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
        conditionsView.add(conditionElement.addClickActions(() ->
        {
            ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
            gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
        }));
        conditionsView.add(new GUITextSpacer(this));

        tabView.recalc(0);
    }
}

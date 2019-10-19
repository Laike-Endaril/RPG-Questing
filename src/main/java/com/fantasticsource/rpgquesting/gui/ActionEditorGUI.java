package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIItemStack;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.mctools.gui.screen.ItemSelectionGUI;
import com.fantasticsource.rpgquesting.Colors;
import com.fantasticsource.rpgquesting.actions.*;
import com.fantasticsource.rpgquesting.actions.quest.CActionCompleteQuest;
import com.fantasticsource.rpgquesting.actions.quest.CActionStartQuest;
import com.fantasticsource.rpgquesting.actions.quest.CQuestAction;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class ActionEditorGUI extends GUIScreen
{
    public CAction original, selection;
    public GUIAction current;
    private GUITextButton delete;
    private GUIText originalLabel, currentLabel;
    private GUITabView tabView;
    private GUIScrollView originalView, currentView, actionOptionsView, requiredConditionsView;
    private GUIVerticalScrollbar originalScrollbar, currentScrollbar;

    public ActionEditorGUI(GUIAction clickedElement)
    {
        this(clickedElement, 1);
    }

    public ActionEditorGUI(GUIAction clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.action;
        selection = original;


        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));


        //Management
        current = new GUIAction(this, original == null ? null : (CAction) original.copy());
        GUITextButton save = new GUITextButton(this, "Save and Close", GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            selection = current.action;

            selection.conditions.clear();
            for (GUIElement element : requiredConditionsView.children)
            {
                if (element instanceof GUICondition)
                {
                    CCondition condition = ((GUICondition) element).condition;
                    if (condition != null) selection.addConditions(condition);
                }
            }

            close();
        }));

        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel.addClickActions(this::close));

        delete = new GUITextButton(this, "Delete Action and Close", RED[0]);
        root.add(delete.addClickActions(() ->
        {
            selection = null;
            close();
        }));


        double free = 1 - delete.height - 0.02;


        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));


        //Labels
        originalLabel = new GUIText(this, 0, 0, "ORIGINAL", Color.YELLOW.copy().setVF(0.2f));
        root.add(originalLabel);
        currentLabel = new GUIText(this, 0, 0, "CURRENT", Color.YELLOW.copy().setVF(0.2f));
        root.add(currentLabel);


        //Original
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalView = new GUIScrollView(this, 0.44, free / 3);
        root.add(originalView);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, originalView);
        root.add(originalScrollbar);

        originalView.add(new GUIText(this, "\n"));
        GUIAction originalElement = new GUIAction(this, current.action == null ? null : (CAction) current.action.copy());
        originalView.add(originalElement.addClickActions(() -> setCurrent(originalElement.action)));
        originalView.add(new GUIText(this, "\n"));


        //Current
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        currentView = new GUIScrollView(this, 0.44, free / 3);
        root.add(currentView);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        currentScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, currentView);
        root.add(currentScrollbar);

        currentView.add(new GUIText(this, "\n"));
        currentView.add(current);
        currentView.add(new GUIText(this, "\n"));


        //Tabview
        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));

        tabView = new GUITabView(this, 1, free * 2 / 3, "Base Action Type", "Action Options", "Required Conditions");
        root.add(tabView);


        //Base Action Type tab
        GUIScrollView actionTypeView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(0).add(actionTypeView);
        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, actionTypeView));

        //Dialogue actions
        actionTypeView.add(new GUIText(this, "\n"));

        actionTypeView.add(new CActionBranch().getChoosableElement(this));
        actionTypeView.add(new GUIText(this, "\n"));

        actionTypeView.add(new CActionEndDialogue().getChoosableElement(this));
        actionTypeView.add(new GUIText(this, "\n"));

        //Quest actions
        actionTypeView.add(new GUIText(this, "\n"));

        actionTypeView.add(new CActionStartQuest().getChoosableElement(this));
        actionTypeView.add(new GUIText(this, "\n"));

        actionTypeView.add(new CActionCompleteQuest().getChoosableElement(this));
        actionTypeView.add(new GUIText(this, "\n"));

        //Normal actions
        actionTypeView.add(new GUIText(this, "\n"));

        actionTypeView.add(new CActionTakeItems().getChoosableElement(this));
        actionTypeView.add(new GUIText(this, "\n"));

        //Meta actions
        actionTypeView.add(new GUIText(this, "\n"));

        actionTypeView.add(new CActionArray().getChoosableElement(this));
        actionTypeView.add(new GUIText(this, "\n"));

        for (int i = actionTypeView.size() - 1; i >= 0; i--)
        {
            GUIElement element = actionTypeView.get(i);
            if (element instanceof GUIAction)
            {
                element.addClickActions(() ->
                {
                    setCurrent(((GUIAction) element).action);
                    tabView.setActiveTab(1);
                });
            }
        }


        //Action Options tab
        actionOptionsView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(1).add(actionOptionsView);
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, actionOptionsView));

        setCurrent(selection);


        //Required Conditions tab
        requiredConditionsView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(2).add(requiredConditionsView);
        tabView.tabViews.get(2).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, requiredConditionsView));

        if (selection != null)
        {
            for (CCondition condition : selection.conditions)
            {
                requiredConditionsView.add(new GUIText(this, "\n"));
                GUICondition conditionElement = new GUICondition(this, condition);
                requiredConditionsView.add(conditionElement.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                    gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
                }));
            }
        }

        {
            requiredConditionsView.add(new GUIText(this, "\n"));
            GUICondition conditionElement = new GUICondition(this, null);
            conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
            requiredConditionsView.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
            }));
        }

        if (selection != null && selection.conditions.size() > 0)
        {
            requiredConditionsView.add(new GUIText(this, "\n"));
            requiredConditionsView.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
        }

        requiredConditionsView.add(new GUIText(this, "\n"));


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);

        double free = 1 - delete.height - 0.02;


        //Resize views and scrollbars
        originalView.height = free / 3;
        originalScrollbar.height = free / 3;

        currentView.height = free / 3;
        currentScrollbar.height = free / 3;

        tabView.height = free * 2 / 3;


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;


        root.recalc();
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CAction action)
    {
        current.setAction(action);


        actionOptionsView.clear();

        actionOptionsView.add(new GUIText(this, "\n"));

        if (action != null)
        {
            Class cls = action.getClass();
            if (cls == CActionEndDialogue.class)
            {
                actionOptionsView.add(new GUIText(this, "(No options)\n"));
                actionOptionsView.add(new GUIText(this, "\n"));
            }
            else if (cls == CActionBranch.class)
            {
                GUILabeledTextInput dialogueName = new GUILabeledTextInput(this, "Dialogue Name: ", ((CActionBranch) action).dialogueName.value, FilterNotEmpty.INSTANCE);
                dialogueName.input.addRecalcActions(() ->
                {
                    if (dialogueName.input.valid())
                    {
                        ((CActionBranch) action).dialogueName.set(dialogueName.input.text);
                        current.setAction(action);
                    }
                });
                actionOptionsView.add(dialogueName);
                actionOptionsView.add(new GUIText(this, "\n"));

                GUILabeledTextInput branchIndex = new GUILabeledTextInput(this, "Branch Index: ", "" + ((CActionBranch) action).branchIndex.value, FilterInt.INSTANCE);
                branchIndex.input.addRecalcActions(() ->
                {
                    if (branchIndex.input.valid())
                    {
                        ((CActionBranch) action).branchIndex.set(FilterInt.INSTANCE.parse(branchIndex.input.text));
                        current.setAction(action);
                    }
                });
                actionOptionsView.add(branchIndex);
                actionOptionsView.add(new GUIText(this, "\n"));
            }
            else if (action instanceof CQuestAction)
            {
                GUILabeledTextInput questName = new GUILabeledTextInput(this, "Quest name: ", "" + ((CQuestAction) action).questName.value, FilterNotEmpty.INSTANCE);
                questName.input.addRecalcActions(() ->
                {
                    if (questName.input.valid())
                    {
                        ((CQuestAction) action).questName.set(questName.input.text);
                        current.setAction(action);
                    }
                });
                actionOptionsView.add(questName);
                actionOptionsView.add(new GUIText(this, "\n"));
            }
            else if (cls == CActionTakeItems.class)
            {
                CActionTakeItems haveItems = (CActionTakeItems) action;
                GUIItemStack stackElement = new GUIItemStack(this, haveItems.stackToMatch.value);
                actionOptionsView.add(stackElement.addClickActions(() ->
                {
                    ItemSelectionGUI gui = new ItemSelectionGUI(stackElement, textScale);
                    gui.addOnClosedActions(() ->
                    {
                        stackElement.setStack(gui.selection);
                        haveItems.set(gui.selection);
                        current.setAction(haveItems);
                    });
                }));
                actionOptionsView.add(new GUIText(this, "\n"));
            }
            else if (cls == CActionArray.class)
            {
                CActionArray and = (CActionArray) action;
                for (int i = 0; i < and.actions.size(); i++)
                {
                    CAction subAction = and.actions.get(i);
                    GUIAction subActionElement = new GUIAction(this, subAction);
                    actionOptionsView.add(subActionElement.addClickActions(() ->
                    {
                        ActionEditorGUI gui = new ActionEditorGUI(subActionElement, textScale);
                        gui.addOnClosedActions(() ->
                        {
                            if (gui.selection == null)
                            {
                                int index = actionOptionsView.indexOf(subActionElement);
                                actionOptionsView.remove(index);
                                actionOptionsView.remove(index);
                                and.actions.remove(subAction);
                            }
                            else
                            {
                                subActionElement.setAction(gui.selection);
                                and.actions.set(and.actions.indexOf(subAction), gui.selection);
                            }
                            current.setAction(and);
                        });
                    }));
                    actionOptionsView.add(new GUIText(this, "\n"));
                }

                GUIAction subActionElement = new GUIAction(this, null);
                actionOptionsView.add(subActionElement.addClickActions(() ->
                {
                    ActionEditorGUI gui = new ActionEditorGUI(subActionElement, textScale);
                    gui.addOnClosedActions(() ->
                    {
                        if (gui.selection != null)
                        {
                            int index = actionOptionsView.size() - 2;
                            actionOptionsView.add(index, new GUIText(this, "\n"));
                            CAction subAction = gui.selection;
                            GUIAction subActionElement2 = new GUIAction(this, subAction);
                            actionOptionsView.add(index, subActionElement2.addClickActions(() ->
                            {
                                ActionEditorGUI gui2 = new ActionEditorGUI(subActionElement2, textScale);
                                gui2.addOnClosedActions(() ->
                                {
                                    if (gui2.selection == null)
                                    {
                                        int index2 = actionOptionsView.indexOf(subActionElement);
                                        actionOptionsView.remove(index2);
                                        actionOptionsView.remove(index2);
                                        and.actions.remove(subAction);
                                    }
                                    else
                                    {
                                        subActionElement2.setAction(gui2.selection);
                                        and.actions.set(and.actions.indexOf(subAction), gui2.selection);
                                    }
                                    current.setAction(and);
                                });
                            }));

                            and.actions.add(gui.selection);
                            current.setAction(and);
                        }
                    });
                }));

                actionOptionsView.add(new GUIText(this, "\n"));
            }
        }

        currentView.recalc();
    }


    private void editCondition(GUICondition activeObjectiveElement, CCondition newCondition)
    {
        if (activeObjectiveElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
        {
            //Started with empty slot
            if (newCondition != null)
            {
                //Added new condition
                int index = requiredConditionsView.indexOf(activeObjectiveElement);

                {
                    requiredConditionsView.add(index, new GUIText(this, "\n"));
                    GUICondition conditionElement = new GUICondition(this, (CCondition) newCondition.copy());
                    requiredConditionsView.add(index, conditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
                        gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Objectives were empty, but no longer are
                    requiredConditionsView.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
                    requiredConditionsView.add(new GUIText(this, "\n"));
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
                int index = requiredConditionsView.indexOf(activeObjectiveElement);
                requiredConditionsView.remove(index);
                requiredConditionsView.remove(index);

                if (requiredConditionsView.size() == 5)
                {
                    //Had one objective, and now have 0 (remove the "clear all" option)
                    requiredConditionsView.remove(3);
                    requiredConditionsView.remove(3);
                }
            }
        }
    }

    private void clearConditions()
    {
        requiredConditionsView.clear();

        requiredConditionsView.add(new GUIText(this, "\n"));
        GUICondition conditionElement = new GUICondition(this, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
        requiredConditionsView.add(conditionElement.addClickActions(() ->
        {
            ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement, textScale);
            gui.addOnClosedActions(() -> editCondition(conditionElement, gui.selection));
        }));
        requiredConditionsView.add(new GUIText(this, "\n"));

        tabView.recalc();
    }
}

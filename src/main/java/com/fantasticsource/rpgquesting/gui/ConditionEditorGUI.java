package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.mctools.gui.screen.ItemSelectionGUI;
import com.fantasticsource.rpgquesting.Colors;
import com.fantasticsource.rpgquesting.conditions.*;
import com.fantasticsource.rpgquesting.conditions.quest.*;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class ConditionEditorGUI extends GUIScreen
{
    public CCondition original, selection;
    public GUICondition current;
    private GUITextButton delete;
    private GUITabView tabView;
    private GUIScrollView conditionEditor, currentView;
    private GUIVerticalScrollbar currentScrollbar;

    public ConditionEditorGUI(GUICondition clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.condition;
        selection = original;


        //Background
        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));

        //Navbar
        root.add(new GUINavbar(this, Color.AQUA));

        //Management
        current = new GUICondition(this, original == null ? null : (CCondition) original.copy());
        GUITextButton save = new GUITextButton(this, "Save and Close", GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            selection = current.condition;
            close();
        }));

        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel.addClickActions(() ->
        {
            selection = original;
            close();
        }));

        delete = new GUITextButton(this, "Delete Condition and Close", RED[0]);
        root.add(delete.addClickActions(() ->
        {
            selection = null;
            close();
        }));


        double oneThird = (1 - delete.height - 0.02) / 3;


        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));


        //Current
        GUITextSpacer spacer = new GUITextSpacer(this, oneThird, true);
        currentView = new GUIScrollView(this, 0.98 - spacer.width * 2, oneThird);
        root.add(spacer.addRecalcActions(() -> currentView.width = 0.98 - spacer.width * 2));
        root.add(currentView);

        GUITextSpacer spacer2 = new GUITextSpacer(this, oneThird, true);
        root.add(spacer2);
        currentScrollbar = new GUIVerticalScrollbar(this, 0.02, oneThird, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, currentView);
        root.add(currentScrollbar);

        currentView.add(new GUITextSpacer(this));
        currentView.add(new GUIText(this, TextFormatting.GOLD + "CURRENT CONDITION..."));
        currentView.add(new GUITextSpacer(this));
        currentView.add(current);
        currentView.add(new GUITextSpacer(this));


        //Tabview
        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));

        tabView = new GUITabView(this, 1, oneThird * 2, "Base Condition Type", "Condition Options");
        root.add(tabView);


        //Condition selector
        GUITextSpacer spacer3 = new GUITextSpacer(this, true);
        GUIScrollView conditionSelector = new GUIScrollView(this, 0.98 - spacer3.width * 2, 1);
        tabView.tabViews.get(0).add(spacer3.addRecalcActions(() -> conditionSelector.width = 0.98 - spacer3.width * 2));
        tabView.tabViews.get(0).add(conditionSelector);

        tabView.tabViews.get(0).add(new GUITextSpacer(this, true));
        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionSelector));

        //Quest conditions
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionQuestAvailable().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionQuestInProgress().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionQuestReadyToComplete().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionQuestCompleted().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionQuestDaily().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionQuestWeekly().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        //Normal conditions
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionNameIs().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionEntityEntryIs().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionClassIs().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionInventorySpace().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionHaveItems().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        //Meta conditions
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionAnd().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionNot().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        conditionSelector.add(new CConditionOr().getChoosableElement(this));
        conditionSelector.add(new GUITextSpacer(this));

        for (int i = conditionSelector.size() - 1; i >= 0; i--)
        {
            GUIElement element = conditionSelector.get(i);
            if (element instanceof GUICondition)
            {
                element.addClickActions(() ->
                {
                    setCurrent(((GUICondition) element).condition);
                    tabView.setActiveTab(1);
                });
            }
        }


        //Condition editor
        GUITextSpacer spacer4 = new GUITextSpacer(this, true);
        conditionEditor = new GUIScrollView(this, 0.98 - spacer4.width * 2, 1);
        tabView.tabViews.get(1).add(spacer4.addRecalcActions(() -> conditionEditor.width = 0.98 - spacer4.width * 2));
        tabView.tabViews.get(1).add(conditionEditor);

        tabView.tabViews.get(1).add(new GUITextSpacer(this, true));
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionEditor));


        setCurrent(current.condition);


        //Recalc actions
        delete.addRecalcActions(() ->
        {
            double oneThirdHeight = (1 - delete.height - 0.02) / 3;

            //Resize views and scrollbars
            spacer.height = oneThirdHeight;
            currentView.height = oneThirdHeight;
            spacer2.height = oneThirdHeight;
            currentScrollbar.height = oneThirdHeight;

            tabView.height = oneThirdHeight * 2;
        });
    }

    @Override
    public String title()
    {
        return "Condition";
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CCondition condition)
    {
        current.setCondition(condition);


        conditionEditor.clear();

        conditionEditor.add(new GUITextSpacer(this));

        if (condition != null)
        {
            Class cls = condition.getClass();
            if (cls == CConditionNameIs.class)
            {
                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity name: ", ((CConditionNameIs) condition).name.value, FilterNotEmpty.INSTANCE);
                name.input.addRecalcActions(() ->
                {
                    if (name.input.valid())
                    {
                        ((CConditionNameIs) condition).name.set(name.input.text);
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(name);
                conditionEditor.add(new GUITextSpacer(this));
            }
            else if (cls == CConditionEntityEntryIs.class)
            {
                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity registry name: ", ((CConditionEntityEntryIs) condition).entityEntryName.value, FilterNotEmpty.INSTANCE);
                name.input.addRecalcActions(() ->
                {
                    if (name.input.valid())
                    {
                        ((CConditionEntityEntryIs) condition).entityEntryName.set(name.input.text);
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(name);
                conditionEditor.add(new GUITextSpacer(this));
            }
            else if (cls == CConditionClassIs.class)
            {
                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity class name: ", ((CConditionClassIs) condition).className.value, FilterNotEmpty.INSTANCE);
                name.input.addRecalcActions(() ->
                {
                    if (name.input.valid())
                    {
                        ((CConditionClassIs) condition).className.set(name.input.text);
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(name);
                conditionEditor.add(new GUITextSpacer(this));
            }
            else if (cls == CConditionInventorySpace.class)
            {
                GUILabeledTextInput slotCount = new GUILabeledTextInput(this, "Empty slot count: ", "" + ((CConditionInventorySpace) condition).slotCount.value, FilterInt.INSTANCE);
                slotCount.input.addRecalcActions(() ->
                {
                    if (slotCount.input.valid())
                    {
                        ((CConditionInventorySpace) condition).slotCount.set(Integer.parseInt(slotCount.input.text));
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(slotCount);
                conditionEditor.add(new GUITextSpacer(this));
            }
            else if (cls == CConditionHaveItems.class)
            {
                CConditionHaveItems haveItems = (CConditionHaveItems) condition;
                GUIItemStack stackElement = new GUIItemStack(this, haveItems.stackToMatch.value);
                conditionEditor.add(stackElement.addClickActions(() ->
                {
                    ItemSelectionGUI gui = new ItemSelectionGUI(stackElement, textScale);
                    gui.addOnClosedActions(() ->
                    {
                        stackElement.setStack(gui.selection);
                        haveItems.set(gui.selection);
                        current.setCondition(haveItems);
                    });
                }));
                conditionEditor.add(new GUITextSpacer(this));
            }
            else if (condition instanceof CQuestCondition)
            {
                GUILabeledTextInput questName = new GUILabeledTextInput(this, "Quest name: ", "" + ((CQuestCondition) condition).questName.value, FilterNotEmpty.INSTANCE);
                questName.input.addRecalcActions(() ->
                {
                    if (questName.input.valid())
                    {
                        ((CQuestCondition) condition).questName.set(questName.input.text);
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(questName);
                conditionEditor.add(new GUITextSpacer(this));

                if (cls == CConditionQuestDaily.class)
                {
                    GUILabeledTextInput hourOffset = new GUILabeledTextInput(this, "Reset hour (12 is noon): ", "" + ((CConditionQuestDaily) condition).hourOffset.value, FilterInt.INSTANCE);
                    hourOffset.input.addRecalcActions(() ->
                    {
                        if (hourOffset.input.valid())
                        {
                            ((CConditionQuestDaily) condition).hourOffset.set(Integer.parseInt(hourOffset.input.text));
                            current.setCondition(condition);
                        }
                    });
                    conditionEditor.add(hourOffset);
                    conditionEditor.add(new GUITextSpacer(this));

                    questName.input.text = QuestEditorGUI.GUI.name.input.text;
                }
                else if (cls == CConditionQuestWeekly.class)
                {
                    GUILabeledTextInput hourOffset = new GUILabeledTextInput(this, "Reset hour (12 is noon Monday, 36 is noon Tuesday): ", "" + ((CConditionQuestWeekly) condition).hourOffset.value, FilterInt.INSTANCE);
                    hourOffset.input.addRecalcActions(() ->
                    {
                        if (hourOffset.input.valid())
                        {
                            ((CConditionQuestWeekly) condition).hourOffset.set(Integer.parseInt(hourOffset.input.text));
                            current.setCondition(condition);
                        }
                    });
                    conditionEditor.add(hourOffset);
                    conditionEditor.add(new GUITextSpacer(this));

                    questName.input.text = QuestEditorGUI.GUI.name.input.text;
                }
            }
            else if (cls == CConditionNot.class)
            {
                CConditionNot not = (CConditionNot) condition;
                GUICondition subConditionElement = new GUICondition(this, not.condition);
                conditionEditor.add(subConditionElement.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(subConditionElement, textScale);
                    gui.addOnClosedActions(() ->
                    {
                        subConditionElement.setCondition(gui.selection);
                        not.condition = gui.selection;
                        current.setCondition(not);
                    });
                }));
                conditionEditor.add(new GUITextSpacer(this));
            }
            else if (cls == CConditionAnd.class)
            {
                CConditionAnd and = (CConditionAnd) condition;
                for (int i = 0; i < and.conditions.size(); i++)
                {
                    CCondition subCondition = and.conditions.get(i);
                    GUICondition subConditionElement = new GUICondition(this, subCondition);
                    conditionEditor.add(subConditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(subConditionElement, textScale);
                        gui.addOnClosedActions(() ->
                        {
                            if (gui.selection == null)
                            {
                                int index = conditionEditor.indexOf(subConditionElement);
                                conditionEditor.remove(index);
                                conditionEditor.remove(index);
                                and.conditions.remove(subCondition);
                            }
                            else
                            {
                                subConditionElement.setCondition(gui.selection);
                                and.conditions.set(and.conditions.indexOf(subCondition), gui.selection);
                            }
                            current.setCondition(and);
                        });
                    }));
                    conditionEditor.add(new GUITextSpacer(this));
                }

                GUICondition subConditionElement = new GUICondition(this, null);
                conditionEditor.add(subConditionElement.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(subConditionElement, textScale);
                    gui.addOnClosedActions(() ->
                    {
                        if (gui.selection != null)
                        {
                            int index = conditionEditor.size() - 2;
                            conditionEditor.add(index, new GUITextSpacer(this));
                            CCondition subCondition = gui.selection;
                            GUICondition subConditionElement2 = new GUICondition(this, subCondition);
                            conditionEditor.add(index, subConditionElement2.addClickActions(() ->
                            {
                                ConditionEditorGUI gui2 = new ConditionEditorGUI(subConditionElement2, textScale);
                                gui2.addOnClosedActions(() ->
                                {
                                    if (gui2.selection == null)
                                    {
                                        int index2 = conditionEditor.indexOf(subConditionElement);
                                        conditionEditor.remove(index2);
                                        conditionEditor.remove(index2);
                                        and.conditions.remove(subCondition);
                                    }
                                    else
                                    {
                                        subConditionElement2.setCondition(gui2.selection);
                                        and.conditions.set(and.conditions.indexOf(subCondition), gui2.selection);
                                    }
                                    current.setCondition(and);
                                });
                            }));

                            and.conditions.add(gui.selection);
                            current.setCondition(and);
                        }
                    });
                }));

                conditionEditor.add(new GUITextSpacer(this));
            }
            else if (cls == CConditionOr.class)
            {
                CConditionOr or = (CConditionOr) condition;
                for (int i = 0; i < or.conditions.size(); i++)
                {
                    CCondition subCondition = or.conditions.get(i);
                    GUICondition subConditionElement = new GUICondition(this, subCondition);
                    conditionEditor.add(subConditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(subConditionElement, textScale);
                        gui.addOnClosedActions(() ->
                        {
                            if (gui.selection == null)
                            {
                                int index = conditionEditor.indexOf(subConditionElement);
                                conditionEditor.remove(index);
                                conditionEditor.remove(index);
                                or.conditions.remove(subCondition);
                            }
                            else
                            {
                                subConditionElement.setCondition(gui.selection);
                                or.conditions.set(or.conditions.indexOf(subCondition), gui.selection);
                            }
                            current.setCondition(or);
                        });
                    }));
                    conditionEditor.add(new GUITextSpacer(this));
                }

                GUICondition subConditionElement = new GUICondition(this, null);
                conditionEditor.add(subConditionElement.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(subConditionElement, textScale);
                    gui.addOnClosedActions(() ->
                    {
                        if (gui.selection != null)
                        {
                            int index = conditionEditor.size() - 2;
                            conditionEditor.add(index, new GUITextSpacer(this));
                            CCondition subCondition = gui.selection;
                            GUICondition subConditionElement2 = new GUICondition(this, subCondition);
                            conditionEditor.add(index, subConditionElement2.addClickActions(() ->
                            {
                                ConditionEditorGUI gui2 = new ConditionEditorGUI(subConditionElement2, textScale);
                                gui2.addOnClosedActions(() ->
                                {
                                    if (gui2.selection == null)
                                    {
                                        int index2 = conditionEditor.indexOf(subConditionElement);
                                        conditionEditor.remove(index2);
                                        conditionEditor.remove(index2);
                                        or.conditions.remove(subCondition);
                                    }
                                    else
                                    {
                                        subConditionElement2.setCondition(gui2.selection);
                                        or.conditions.set(or.conditions.indexOf(subCondition), gui2.selection);
                                    }
                                    current.setCondition(or);
                                });
                            }));

                            or.conditions.add(gui.selection);
                            current.setCondition(or);
                        }
                    });
                }));

                conditionEditor.add(new GUITextSpacer(this));
            }
        }

        currentView.recalc(0);
    }
}

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
import com.fantasticsource.mctools.gui.screen.ItemSelectionGUI;
import com.fantasticsource.rpgquesting.conditions.*;
import com.fantasticsource.rpgquesting.conditions.quest.*;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import static com.fantasticsource.rpgquesting.gui.JournalGUI.RED;

public class ConditionEditorGUI extends GUIScreen
{
    public CCondition original, selection;
    public GUICondition current;
    private GUITextButton delete;
    private GUIText originalLabel, currentLabel, conditionSelectorLabel, conditionEditorLabel;
    private GUIGradientBorder[] separators = new GUIGradientBorder[4];
    private GUIScrollView conditionSelector, conditionEditor, originalView, currentView;
    private GUIVerticalScrollbar conditionSelectorScrollbar, conditionEditorScrollbar, originalScrollbar, currentScrollbar;

    public ConditionEditorGUI(GUICondition clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.condition;
        selection = original;


        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));


        //Management
        current = new GUICondition(this, original);
        GUITextButton save = new GUITextButton(this, "Save and Close", JournalGUI.GREEN[0]);
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


        double free = 1 - delete.height - 0.03;


        separators[0] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[0]);


        //Labels
        originalLabel = new GUIText(this, 0, 0, "ORIGINAL", Color.YELLOW.copy().setVF(0.2f));
        root.add(originalLabel);
        currentLabel = new GUIText(this, 0, 0, "CURRENT", Color.YELLOW.copy().setVF(0.2f));
        root.add(currentLabel);
        conditionSelectorLabel = new GUIText(this, 0, 0, "CONDITION SELECTION", Color.YELLOW.copy().setVF(0.2f));
        root.add(conditionSelectorLabel);
        conditionEditorLabel = new GUIText(this, 0, 0, "CONDITION EDITING", Color.YELLOW.copy().setVF(0.2f));
        root.add(conditionEditorLabel);


        //Original
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalView = new GUIScrollView(this, 0.44, free / 3);
        root.add(originalView);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, originalView);
        root.add(originalScrollbar);

        originalView.add(new GUIText(this, "\n"));
        GUICondition originalElement = new GUICondition(this, current.condition == null ? null : (CCondition) current.condition.copy());
        originalView.add(originalElement.addClickActions(() -> setCurrent(originalElement.condition)));
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


        separators[2] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[2]);


        //Condition selector
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        conditionSelector = new GUIScrollView(this, 0.94, free / 3);
        root.add(conditionSelector);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        conditionSelectorScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionSelector);
        root.add(conditionSelectorScrollbar);

        //Quest conditions
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionQuestAvailable().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionQuestInProgress().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionQuestReadyToComplete().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionQuestCompleted().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        //Normal conditions
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionNameIs().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionEntityEntryIs().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionClassIs().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionInventorySpace().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionHaveItems().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        //Meta conditions
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionAnd().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionNot().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionOr().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        for (int i = conditionSelector.size() - 1; i >= 0; i--)
        {
            GUIElement element = conditionSelector.get(i);
            if (element instanceof GUICondition)
            {
                element.addClickActions(() -> setCurrent(((GUICondition) element).condition));
            }
        }


        separators[3] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[3]);


        //Condition editor
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        conditionEditor = new GUIScrollView(this, 0.94, free / 3);
        root.add(conditionEditor);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));

        conditionEditorScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionEditor);
        root.add(conditionEditorScrollbar);

        setCurrent(current.condition);


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;

        conditionSelectorLabel.x = conditionSelector.x + conditionSelector.width / 2 - conditionSelectorLabel.width / 2;
        conditionSelectorLabel.y = conditionSelector.y + conditionSelector.height / 2 - conditionSelectorLabel.height / 2;

        conditionEditorLabel.x = conditionEditor.x + conditionEditor.width / 2 - conditionEditorLabel.width / 2;
        conditionEditorLabel.y = conditionEditor.y + conditionEditor.height / 2 - conditionEditorLabel.height / 2;
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);

        double free = 1 - delete.height - 0.03;


        //Resize views and scrollbars
        originalView.height = free / 3;
        originalScrollbar.height = free / 3;

        currentView.height = free / 3;
        currentScrollbar.height = free / 3;

        conditionSelector.height = free / 3;
        conditionSelectorScrollbar.height = free / 3;

        conditionEditor.height = free / 3;
        conditionEditorScrollbar.height = free / 3;


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;

        conditionSelectorLabel.x = conditionSelector.x + conditionSelector.width / 2 - conditionSelectorLabel.width / 2;
        conditionSelectorLabel.y = conditionSelector.y + conditionSelector.height / 2 - conditionSelectorLabel.height / 2;

        conditionEditorLabel.x = conditionEditor.x + conditionEditor.width / 2 - conditionEditorLabel.width / 2;
        conditionEditorLabel.y = conditionEditor.y + conditionEditor.height / 2 - conditionEditorLabel.height / 2;


        root.recalc();
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CCondition condition)
    {
        current.setCondition(condition);


        conditionEditor.clear();

        conditionEditor.add(new GUIText(this, "\n"));

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
                conditionEditor.add(new GUIText(this, "\n"));
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
                conditionEditor.add(new GUIText(this, "\n"));
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
                conditionEditor.add(new GUIText(this, "\n"));
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
                conditionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CConditionHaveItems.class)
            {
                CConditionHaveItems haveItems = (CConditionHaveItems) condition;
                GUIItemStack stackElement = new GUIItemStack(this, haveItems.stackToMatch.stack);
                conditionEditor.add(stackElement.addClickActions(() ->
                {
                    ItemSelectionGUI gui = new ItemSelectionGUI(stackElement);
                    gui.addOnClosedActions(() ->
                    {
                        stackElement.setStack(gui.selection);
                        haveItems.set(gui.selection);
                        current.setCondition(haveItems);
                    });
                }));
                conditionEditor.add(new GUIText(this, "\n"));
            }
            else if (condition instanceof CQuestCondition)
            {
                GUILabeledTextInput questName = new GUILabeledTextInput(this, "Quest name: ", "" + ((CQuestCondition) condition).name.value, FilterNotEmpty.INSTANCE);
                questName.input.addRecalcActions(() ->
                {
                    if (questName.input.valid())
                    {
                        ((CQuestCondition) condition).name.set(questName.input.text);
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(questName);
                conditionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CConditionNot.class)
            {
                CConditionNot not = (CConditionNot) condition;
                GUICondition subConditionElement = new GUICondition(this, not.condition);
                conditionEditor.add(subConditionElement.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(subConditionElement);
                    gui.addOnClosedActions(() ->
                    {
                        subConditionElement.setCondition(gui.selection);
                        not.condition = gui.selection;
                        current.setCondition(not);
                    });
                }));
                conditionEditor.add(new GUIText(this, "\n"));
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
                        ConditionEditorGUI gui = new ConditionEditorGUI(subConditionElement);
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
                    conditionEditor.add(new GUIText(this, "\n"));
                }

                GUICondition subConditionElement = new GUICondition(this, null);
                conditionEditor.add(subConditionElement.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(subConditionElement);
                    gui.addOnClosedActions(() ->
                    {
                        if (gui.selection != null)
                        {
                            int index = conditionEditor.size() - 2;
                            conditionEditor.add(index, new GUIText(this, "\n"));
                            CCondition subCondition = gui.selection;
                            GUICondition subConditionElement2 = new GUICondition(this, subCondition);
                            conditionEditor.add(index, subConditionElement2.addClickActions(() ->
                            {
                                ConditionEditorGUI gui2 = new ConditionEditorGUI(subConditionElement2);
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

                conditionEditor.add(new GUIText(this, "\n"));
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
                        ConditionEditorGUI gui = new ConditionEditorGUI(subConditionElement);
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
                    conditionEditor.add(new GUIText(this, "\n"));
                }

                GUICondition subConditionElement = new GUICondition(this, null);
                conditionEditor.add(subConditionElement.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(subConditionElement);
                    gui.addOnClosedActions(() ->
                    {
                        if (gui.selection != null)
                        {
                            int index = conditionEditor.size() - 2;
                            conditionEditor.add(index, new GUIText(this, "\n"));
                            CCondition subCondition = gui.selection;
                            GUICondition subConditionElement2 = new GUICondition(this, subCondition);
                            conditionEditor.add(index, subConditionElement2.addClickActions(() ->
                            {
                                ConditionEditorGUI gui2 = new ConditionEditorGUI(subConditionElement2);
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

                conditionEditor.add(new GUIText(this, "\n"));
            }
        }

        currentView.recalc();
    }
}

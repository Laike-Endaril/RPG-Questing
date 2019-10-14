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
import com.fantasticsource.rpgquesting.actions.*;
import com.fantasticsource.rpgquesting.actions.quest.CActionCompleteQuest;
import com.fantasticsource.rpgquesting.actions.quest.CActionStartQuest;
import com.fantasticsource.rpgquesting.actions.quest.CQuestAction;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class ActionEditorGUI extends GUIScreen
{
    public CAction original, selection;
    public GUIAction current;
    private GUITextButton delete;
    private GUIText originalLabel, currentLabel, actionSelectorLabel, actionEditorLabel;
    private GUIScrollView actionSelector, actionEditor, originalView, currentView;
    private GUIVerticalScrollbar actionSelectorScrollbar, actionEditorScrollbar, originalScrollbar, currentScrollbar;

    public ActionEditorGUI(GUIAction clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.action;
        selection = original;


        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));


        //Management
        current = new GUIAction(this, original == null ? null : (CAction) original.copy());
        GUITextButton save = new GUITextButton(this, "Save and Close", GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            selection = current.action;
            close();
        }));

        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel.addClickActions(() ->
        {
            selection = original;
            close();
        }));

        delete = new GUITextButton(this, "Delete Action and Close", RED[0]);
        root.add(delete.addClickActions(() ->
        {
            selection = null;
            close();
        }));


        double free = 1 - delete.height - 0.03;


        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));


        //Labels
        originalLabel = new GUIText(this, 0, 0, "ORIGINAL", Color.YELLOW.copy().setVF(0.2f));
        root.add(originalLabel);
        currentLabel = new GUIText(this, 0, 0, "CURRENT", Color.YELLOW.copy().setVF(0.2f));
        root.add(currentLabel);
        actionSelectorLabel = new GUIText(this, 0, 0, "ACTION SELECTION", Color.YELLOW.copy().setVF(0.2f));
        root.add(actionSelectorLabel);
        actionEditorLabel = new GUIText(this, 0, 0, "ACTION EDITING", Color.YELLOW.copy().setVF(0.2f));
        root.add(actionEditorLabel);


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


        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));


        //Action selector
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        actionSelector = new GUIScrollView(this, 0.94, free / 3);
        root.add(actionSelector);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        actionSelectorScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, actionSelector);
        root.add(actionSelectorScrollbar);

        //Dialogue actions
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionBranch().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionEndDialogue().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        //Quest actions
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionStartQuest().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionCompleteQuest().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        //Normal actions
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionTakeItems().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        //Meta actions
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionArray().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        for (int i = actionSelector.size() - 1; i >= 0; i--)
        {
            GUIElement element = actionSelector.get(i);
            if (element instanceof GUIAction)
            {
                element.addClickActions(() -> setCurrent(((GUIAction) element).action));
            }
        }


        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));


        //Action editor
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        actionEditor = new GUIScrollView(this, 0.94, free / 3);
        root.add(actionEditor);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));

        actionEditorScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, actionEditor);
        root.add(actionEditorScrollbar);

        setCurrent(current.action);


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;

        actionSelectorLabel.x = actionSelector.x + actionSelector.width / 2 - actionSelectorLabel.width / 2;
        actionSelectorLabel.y = actionSelector.y + actionSelector.height / 2 - actionSelectorLabel.height / 2;

        actionEditorLabel.x = actionEditor.x + actionEditor.width / 2 - actionEditorLabel.width / 2;
        actionEditorLabel.y = actionEditor.y + actionEditor.height / 2 - actionEditorLabel.height / 2;
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

        actionSelector.height = free / 3;
        actionSelectorScrollbar.height = free / 3;

        actionEditor.height = free / 3;
        actionEditorScrollbar.height = free / 3;


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;

        actionSelectorLabel.x = actionSelector.x + actionSelector.width / 2 - actionSelectorLabel.width / 2;
        actionSelectorLabel.y = actionSelector.y + actionSelector.height / 2 - actionSelectorLabel.height / 2;

        actionEditorLabel.x = actionEditor.x + actionEditor.width / 2 - actionEditorLabel.width / 2;
        actionEditorLabel.y = actionEditor.y + actionEditor.height / 2 - actionEditorLabel.height / 2;


        root.recalc();
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CAction action)
    {
        current.setAction(action);


        actionEditor.clear();

        actionEditor.add(new GUIText(this, "\n"));

        if (action != null)
        {
            Class cls = action.getClass();
            if (cls == CActionNameIs.class)
            {
                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity name: ", ((CActionNameIs) action).name.value, FilterNotEmpty.INSTANCE);
                name.input.addRecalcActions(() ->
                {
                    if (name.input.valid())
                    {
                        ((CActionNameIs) action).name.set(name.input.text);
                        current.setAction(action);
                    }
                });
                actionEditor.add(name);
                actionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CActionEntityEntryIs.class)
            {
                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity registry name: ", ((CActionEntityEntryIs) action).entityEntryName.value, FilterNotEmpty.INSTANCE);
                name.input.addRecalcActions(() ->
                {
                    if (name.input.valid())
                    {
                        ((CActionEntityEntryIs) action).entityEntryName.set(name.input.text);
                        current.setAction(action);
                    }
                });
                actionEditor.add(name);
                actionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CActionClassIs.class)
            {
                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity class name: ", ((CActionClassIs) action).className.value, FilterNotEmpty.INSTANCE);
                name.input.addRecalcActions(() ->
                {
                    if (name.input.valid())
                    {
                        ((CActionClassIs) action).className.set(name.input.text);
                        current.setAction(action);
                    }
                });
                actionEditor.add(name);
                actionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CActionInventorySpace.class)
            {
                GUILabeledTextInput slotCount = new GUILabeledTextInput(this, "Empty slot count: ", "" + ((CActionInventorySpace) action).slotCount.value, FilterInt.INSTANCE);
                slotCount.input.addRecalcActions(() ->
                {
                    if (slotCount.input.valid())
                    {
                        ((CActionInventorySpace) action).slotCount.set(Integer.parseInt(slotCount.input.text));
                        current.setAction(action);
                    }
                });
                actionEditor.add(slotCount);
                actionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CActionHaveItems.class)
            {
                CActionHaveItems haveItems = (CActionHaveItems) action;
                GUIItemStack stackElement = new GUIItemStack(this, haveItems.stackToMatch.stack);
                actionEditor.add(stackElement.addClickActions(() ->
                {
                    ItemSelectionGUI gui = new ItemSelectionGUI(stackElement);
                    gui.addOnClosedActions(() ->
                    {
                        stackElement.setStack(gui.selection);
                        haveItems.set(gui.selection);
                        current.setAction(haveItems);
                    });
                }));
                actionEditor.add(new GUIText(this, "\n"));
            }
            else if (action instanceof CQuestAction)
            {
                GUILabeledTextInput questName = new GUILabeledTextInput(this, "Quest name: ", "" + ((CQuestAction) action).name.value, FilterNotEmpty.INSTANCE);
                questName.input.addRecalcActions(() ->
                {
                    if (questName.input.valid())
                    {
                        ((CQuestAction) action).name.set(questName.input.text);
                        current.setAction(action);
                    }
                });
                actionEditor.add(questName);
                actionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CActionNot.class)
            {
                CActionNot not = (CActionNot) action;
                GUIAction subActionElement = new GUIAction(this, not.action);
                actionEditor.add(subActionElement.addClickActions(() ->
                {
                    ActionEditorGUI gui = new ActionEditorGUI(subActionElement);
                    gui.addOnClosedActions(() ->
                    {
                        subActionElement.setAction(gui.selection);
                        not.action = gui.selection;
                        current.setAction(not);
                    });
                }));
                actionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CActionAnd.class)
            {
                CActionAnd and = (CActionAnd) action;
                for (int i = 0; i < and.actions.size(); i++)
                {
                    CAction subAction = and.actions.get(i);
                    GUIAction subActionElement = new GUIAction(this, subAction);
                    actionEditor.add(subActionElement.addClickActions(() ->
                    {
                        ActionEditorGUI gui = new ActionEditorGUI(subActionElement);
                        gui.addOnClosedActions(() ->
                        {
                            if (gui.selection == null)
                            {
                                int index = actionEditor.indexOf(subActionElement);
                                actionEditor.remove(index);
                                actionEditor.remove(index);
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
                    actionEditor.add(new GUIText(this, "\n"));
                }

                GUIAction subActionElement = new GUIAction(this, null);
                actionEditor.add(subActionElement.addClickActions(() ->
                {
                    ActionEditorGUI gui = new ActionEditorGUI(subActionElement);
                    gui.addOnClosedActions(() ->
                    {
                        if (gui.selection != null)
                        {
                            int index = actionEditor.size() - 2;
                            actionEditor.add(index, new GUIText(this, "\n"));
                            CAction subAction = gui.selection;
                            GUIAction subActionElement2 = new GUIAction(this, subAction);
                            actionEditor.add(index, subActionElement2.addClickActions(() ->
                            {
                                ActionEditorGUI gui2 = new ActionEditorGUI(subActionElement2);
                                gui2.addOnClosedActions(() ->
                                {
                                    if (gui2.selection == null)
                                    {
                                        int index2 = actionEditor.indexOf(subActionElement);
                                        actionEditor.remove(index2);
                                        actionEditor.remove(index2);
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

                actionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CActionOr.class)
            {
                CActionOr or = (CActionOr) action;
                for (int i = 0; i < or.actions.size(); i++)
                {
                    CAction subAction = or.actions.get(i);
                    GUIAction subActionElement = new GUIAction(this, subAction);
                    actionEditor.add(subActionElement.addClickActions(() ->
                    {
                        ActionEditorGUI gui = new ActionEditorGUI(subActionElement);
                        gui.addOnClosedActions(() ->
                        {
                            if (gui.selection == null)
                            {
                                int index = actionEditor.indexOf(subActionElement);
                                actionEditor.remove(index);
                                actionEditor.remove(index);
                                or.actions.remove(subAction);
                            }
                            else
                            {
                                subActionElement.setAction(gui.selection);
                                or.actions.set(or.actions.indexOf(subAction), gui.selection);
                            }
                            current.setAction(or);
                        });
                    }));
                    actionEditor.add(new GUIText(this, "\n"));
                }

                GUIAction subActionElement = new GUIAction(this, null);
                actionEditor.add(subActionElement.addClickActions(() ->
                {
                    ActionEditorGUI gui = new ActionEditorGUI(subActionElement);
                    gui.addOnClosedActions(() ->
                    {
                        if (gui.selection != null)
                        {
                            int index = actionEditor.size() - 2;
                            actionEditor.add(index, new GUIText(this, "\n"));
                            CAction subAction = gui.selection;
                            GUIAction subActionElement2 = new GUIAction(this, subAction);
                            actionEditor.add(index, subActionElement2.addClickActions(() ->
                            {
                                ActionEditorGUI gui2 = new ActionEditorGUI(subActionElement2);
                                gui2.addOnClosedActions(() ->
                                {
                                    if (gui2.selection == null)
                                    {
                                        int index2 = actionEditor.indexOf(subActionElement);
                                        actionEditor.remove(index2);
                                        actionEditor.remove(index2);
                                        or.actions.remove(subAction);
                                    }
                                    else
                                    {
                                        subActionElement2.setAction(gui2.selection);
                                        or.actions.set(or.actions.indexOf(subAction), gui2.selection);
                                    }
                                    current.setAction(or);
                                });
                            }));

                            or.actions.add(gui.selection);
                            current.setAction(or);
                        }
                    });
                }));

                actionEditor.add(new GUIText(this, "\n"));
            }
        }

        currentView.recalc();
    }
}

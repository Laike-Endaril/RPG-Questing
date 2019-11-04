package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBoolean;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.screen.ItemSelectionGUI;
import com.fantasticsource.rpgquesting.Colors;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.objective.*;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class ObjectiveEditorGUI extends GUIScreen
{
    public CObjective original, selection;
    public GUIObjective current;
    private GUITextButton delete;
    private GUIText objectiveSelectorLabel, objectiveEditorLabel;
    private GUIScrollView objectiveSelector, objectiveEditor, currentView;
    private GUIVerticalScrollbar objectiveSelectorScrollbar, objectiveEditorScrollbar, currentScrollbar;
    private GUIAutocroppedView conditions;

    public ObjectiveEditorGUI(GUIObjective clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.objective;
        selection = original == null ? null : (CObjective) original.copy();


        //Background
        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));


        //Navbar
        root.add(new GUINavbar(this, Color.AQUA));


        //Management
        current = new GUIObjective(this, selection);
        GUITextButton save = new GUITextButton(this, "Save and Close", GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            selection = current.objective;
            close();
        }));

        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel.addClickActions(() ->
        {
            selection = original;
            close();
        }));

        delete = new GUITextButton(this, "Delete Objective and Close", RED[0]);
        root.add(delete.addClickActions(() ->
        {
            selection = null;
            close();
        }));


        double oneThird = (1 - delete.height - 0.03) / 3;


        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));


        //Labels
        objectiveSelectorLabel = new GUIText(this, 0, 0, "OBJECTIVE SELECTION", Color.YELLOW.copy().setVF(0.2f));
        root.add(objectiveSelectorLabel);
        objectiveEditorLabel = new GUIText(this, 0, 0, "OBJECTIVE EDITING", Color.YELLOW.copy().setVF(0.2f));
        root.add(objectiveEditorLabel);


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
        currentView.add(new GUIText(this, TextFormatting.GOLD + "CURRENT OBJECTIVE..."));
        currentView.add(new GUITextSpacer(this));
        currentView.add(current);
        currentView.add(new GUITextSpacer(this));


        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));


        //Objective selector
        GUITextSpacer spacer3 = new GUITextSpacer(this, oneThird, true);
        objectiveSelector = new GUIScrollView(this, 0.98 - spacer3.width * 2, oneThird);
        root.add(spacer3.addRecalcActions(() -> objectiveSelector.width = 0.98 - spacer3.width * 2));
        root.add(objectiveSelector);

        GUITextSpacer spacer4 = new GUITextSpacer(this, oneThird, true);
        root.add(spacer4);
        objectiveSelectorScrollbar = new GUIVerticalScrollbar(this, 0.02, oneThird, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectiveSelector);
        root.add(objectiveSelectorScrollbar);

        //Objective types
        objectiveSelector.add(new GUITextSpacer(this));

        objectiveSelector.add(new CObjectiveDialogue().getChoosableElement(this));
        objectiveSelector.add(new GUITextSpacer(this));

        objectiveSelector.add(new CObjectiveKill().getChoosableElement(this));
        objectiveSelector.add(new GUITextSpacer(this));

        objectiveSelector.add(new CObjectiveCollect().getChoosableElement(this));
        objectiveSelector.add(new GUITextSpacer(this));

        objectiveSelector.add(new CObjectiveEnterArea().getChoosableElement(this));
        objectiveSelector.add(new GUITextSpacer(this));


        for (int i = objectiveSelector.size() - 1; i >= 0; i--)
        {
            GUIElement element = objectiveSelector.get(i);
            if (element instanceof GUIObjective)
            {
                element.addClickActions(() -> setCurrent(((GUIObjective) element).objective));
            }
        }


        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));


        //Objective editor
        GUITextSpacer spacer5 = new GUITextSpacer(this, oneThird, true);
        objectiveEditor = new GUIScrollView(this, 0.98 - spacer5.width * 2, oneThird);
        root.add(spacer5.addRecalcActions(() -> objectiveEditor.width = 0.98 - spacer5.width * 2));
        root.add(objectiveEditor);

        GUITextSpacer spacer6 = new GUITextSpacer(this, oneThird, true);
        root.add(spacer6);
        objectiveEditorScrollbar = new GUIVerticalScrollbar(this, 0.02, oneThird, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectiveEditor);
        root.add(objectiveEditorScrollbar);

        setCurrent(current.objective);


        //Reposition labels
        objectiveSelectorLabel.x = objectiveSelector.x + objectiveSelector.width / 2 - objectiveSelectorLabel.width / 2;
        objectiveSelectorLabel.y = objectiveSelector.y + objectiveSelector.height / 2 - objectiveSelectorLabel.height / 2;

        objectiveEditorLabel.x = objectiveEditor.x + objectiveEditor.width / 2 - objectiveEditorLabel.width / 2;
        objectiveEditorLabel.y = objectiveEditor.y + objectiveEditor.height / 2 - objectiveEditorLabel.height / 2;


        //Recalc actions
        delete.addRecalcActions(() ->
        {
            double oneThirdHeight = (1 - delete.height - 0.03) / 3;

            //Resize views and scrollbars
            spacer.height = oneThirdHeight;
            currentView.height = oneThirdHeight;
            spacer2.height = oneThirdHeight;
            currentScrollbar.height = oneThirdHeight;

            spacer3.height = oneThirdHeight;
            objectiveSelector.height = oneThirdHeight;
            spacer4.height = oneThirdHeight;
            objectiveSelectorScrollbar.height = oneThirdHeight;

            spacer5.height = oneThirdHeight;
            objectiveEditor.height = oneThirdHeight;
            spacer6.height = oneThirdHeight;
            objectiveEditorScrollbar.height = oneThirdHeight;
        });
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);


        //Reposition labels
        objectiveSelectorLabel.x = objectiveSelector.x + objectiveSelector.width / 2 - objectiveSelectorLabel.width / 2;
        objectiveSelectorLabel.y = objectiveSelector.y + objectiveSelector.height / 2 - objectiveSelectorLabel.height / 2;

        objectiveEditorLabel.x = objectiveEditor.x + objectiveEditor.width / 2 - objectiveEditorLabel.width / 2;
        objectiveEditorLabel.y = objectiveEditor.y + objectiveEditor.height / 2 - objectiveEditorLabel.height / 2;


        root.recalc(0);
    }

    @Override
    public String title()
    {
        return "Objective";
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CObjective objective)
    {
        current.setObjective(objective);


        objectiveEditor.clear();

        objectiveEditor.add(new GUITextSpacer(this));

        if (objective != null)
        {
            //Display
            GUILabeledTextInput progressIsPrefix = new GUILabeledTextInput(this, "Progress is prefix: ", "" + objective.progressIsPrefix.value, FilterBoolean.INSTANCE);
            progressIsPrefix.input.addRecalcActions(() ->
            {
                if (progressIsPrefix.input.valid())
                {
                    objective.progressIsPrefix.set(FilterBoolean.INSTANCE.parse(progressIsPrefix.input.text));
                    current.setObjective(objective);
                }
            });
            objectiveEditor.add(progressIsPrefix);
            objectiveEditor.add(new GUITextSpacer(this));

            GUILabeledTextInput text = new GUILabeledTextInput(this, "Text: ", objective.text.value, FilterNotEmpty.INSTANCE);
            text.input.addRecalcActions(() ->
            {
                if (text.input.valid())
                {
                    objective.text.set(FilterNotEmpty.INSTANCE.parse(text.input.text));
                    current.setObjective(objective);
                }
            });
            objectiveEditor.add(text);
            objectiveEditor.add(new GUITextSpacer(this));


            objectiveEditor.add(new GUITextSpacer(this));


            //Meta flags
            GUILabeledTextInput isRequired = new GUILabeledTextInput(this, "Required: ", "" + objective.isRequired.value, FilterBoolean.INSTANCE);
            isRequired.input.addRecalcActions(() ->
            {
                if (isRequired.input.valid())
                {
                    objective.isRequired.set(FilterBoolean.INSTANCE.parse(isRequired.input.text));
                    current.setObjective(objective);
                }
            });
            objectiveEditor.add(isRequired);
            objectiveEditor.add(new GUITextSpacer(this));

            GUILabeledTextInput isActive = new GUILabeledTextInput(this, "Active: ", "" + objective.isActive.value, FilterBoolean.INSTANCE);
            isActive.input.addRecalcActions(() ->
            {
                if (isActive.input.valid())
                {
                    objective.isActive.set(FilterBoolean.INSTANCE.parse(isActive.input.text));
                    current.setObjective(objective);
                }
            });
            objectiveEditor.add(isActive);
            objectiveEditor.add(new GUITextSpacer(this));

            GUILabeledTextInput isHidden = new GUILabeledTextInput(this, "Hidden: ", "" + objective.isHidden.value, FilterBoolean.INSTANCE);
            isHidden.input.addRecalcActions(() ->
            {
                if (isHidden.input.valid())
                {
                    objective.isHidden.set(FilterBoolean.INSTANCE.parse(isHidden.input.text));
                    current.setObjective(objective);
                }
            });
            objectiveEditor.add(isHidden);
            objectiveEditor.add(new GUITextSpacer(this));


            objectiveEditor.add(new GUITextSpacer(this));


            //Objective-specific options
            Class cls = objective.getClass();
            if (cls == CObjectiveDialogue.class)
            {
                objectiveEditor.add(new GUITextSpacer(this));

                CObjectiveDialogue objectiveDialogue = (CObjectiveDialogue) objective;

                GUILabeledTextInput dialogueName = new GUILabeledTextInput(this, "Dialogue Name: ", "" + objectiveDialogue.dialogueName.value, FilterNotEmpty.INSTANCE);
                dialogueName.input.addRecalcActions(() ->
                {
                    if (dialogueName.input.valid())
                    {
                        objectiveDialogue.dialogueName.set(dialogueName.input.text);
                        current.setObjective(objectiveDialogue);
                    }
                });
                objectiveEditor.add(dialogueName);
                objectiveEditor.add(new GUITextSpacer(this));

                GUILabeledTextInput branchIndex = new GUILabeledTextInput(this, "Branch Index: ", "" + objectiveDialogue.branchIndex.value, FilterInt.INSTANCE);
                branchIndex.input.addRecalcActions(() ->
                {
                    if (branchIndex.input.valid())
                    {
                        objectiveDialogue.branchIndex.set(FilterInt.INSTANCE.parse(branchIndex.input.text));
                        current.setObjective(objectiveDialogue);
                    }
                });
                objectiveEditor.add(branchIndex);
                objectiveEditor.add(new GUITextSpacer(this));
            }
            else if (cls == CObjectiveKill.class)
            {
                objectiveEditor.add(new GUITextSpacer(this));

                CObjectiveKill objectiveKill = (CObjectiveKill) objective;


                if (text.input.text.equals(objectiveKill.getChoosableElement(this).text.replace("(0/X) ", "")))
                {
                    text.input.text = text.input.text.replace("X", "" + objectiveKill.required.value);
                }


                GUILabeledTextInput quantity = new GUILabeledTextInput(this, "Quantity: ", "" + objectiveKill.required.value, FilterInt.INSTANCE);
                quantity.input.addRecalcActions(() ->
                {
                    if (quantity.input.valid())
                    {
                        int prevCount = objectiveKill.required.value;
                        int count = FilterInt.INSTANCE.parse(quantity.input.text);

                        objectiveKill.required.set(count);
                        current.setObjective(objectiveKill);

                        text.input.text = text.input.text.replaceAll("" + prevCount, "" + count);
                        text.recalc(0);
                    }
                });
                objectiveEditor.add(quantity);
                objectiveEditor.add(new GUITextSpacer(this));


                objectiveEditor.add(new GUITextSpacer(this));
                objectiveEditor.add(new GUIText(this, TextFormatting.GOLD + "Conditions...\n"));
                conditions = new GUIAutocroppedView(this);
                objectiveEditor.add(conditions);

                for (CCondition condition : objectiveKill.conditions)
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

                if (objectiveKill.conditions.size() > 0)
                {
                    conditions.add(new GUITextSpacer(this));
                    conditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
                }
            }
            else if (cls == CObjectiveCollect.class)
            {
                CObjectiveCollect objectiveCollect = (CObjectiveCollect) objective;


                GUIItemStack stackToMatch = new GUIItemStack(this, objectiveCollect.stackToMatch.value);
                objectiveEditor.add(stackToMatch);
                objectiveEditor.add(new GUITextSpacer(this));


                GUILabeledTextInput quantity = new GUILabeledTextInput(this, "Quantity: ", "" + objectiveCollect.stackToMatch.value.getCount(), FilterInt.INSTANCE);
                quantity.input.addRecalcActions(() ->
                {
                    if (quantity.input.valid())
                    {
                        int prevCount = objectiveCollect.stackToMatch.value.getCount();
                        int count = FilterInt.INSTANCE.parse(quantity.input.text);

                        objectiveCollect.stackToMatch.value.setCount(count);
                        stackToMatch.setStack(objectiveCollect.stackToMatch.value);
                        stackToMatch.recalc(0);
                        current.setObjective(objectiveCollect);

                        text.input.text = text.input.text.replaceAll("" + prevCount, "" + count);
                        text.recalc(0);
                    }
                });
                objectiveEditor.add(quantity);
                objectiveEditor.add(new GUITextSpacer(this));


                stackToMatch.addClickActions(() ->
                {
                    ItemSelectionGUI gui = new ItemSelectionGUI(stackToMatch, textScale);
                    gui.addOnClosedActions(() ->
                    {
                        stackToMatch.setStack(gui.selection);
                        objectiveCollect.stackToMatch.set(gui.selection);
                        quantity.input.text = "" + gui.selection.getCount();
                        current.setObjective(objectiveCollect);
                        text.input.text = objectiveCollect.getChoosableElement(this).text.replace("(0/X) ", "").replace("X", "" + gui.selection.getCount()).replace("items", gui.selection.getDisplayName());
                        objectiveEditor.recalc(0);
                    });
                });
            }
            else if (cls == CObjectiveEnterArea.class)
            {
                CObjectiveEnterArea objectiveEnterArea = (CObjectiveEnterArea) objective;

                GUILabeledTextInput x1 = new GUILabeledTextInput(this, "X1: ", "" + objectiveEnterArea.coords[0].value, FilterInt.INSTANCE);
                x1.input.addRecalcActions(() ->
                {
                    if (x1.input.valid())
                    {
                        objectiveEnterArea.coords[0].set(FilterInt.INSTANCE.parse(x1.input.text));
                        current.setObjective(objectiveEnterArea);
                    }
                });
                objectiveEditor.add(x1);
                objectiveEditor.add(new GUITextSpacer(this));

                GUILabeledTextInput y1 = new GUILabeledTextInput(this, "Y1: ", "" + objectiveEnterArea.coords[1].value, FilterInt.INSTANCE);
                y1.input.addRecalcActions(() ->
                {
                    if (y1.input.valid())
                    {
                        objectiveEnterArea.coords[1].set(FilterInt.INSTANCE.parse(y1.input.text));
                        current.setObjective(objectiveEnterArea);
                    }
                });
                objectiveEditor.add(y1);
                objectiveEditor.add(new GUITextSpacer(this));

                GUILabeledTextInput z1 = new GUILabeledTextInput(this, "Z1: ", "" + objectiveEnterArea.coords[2].value, FilterInt.INSTANCE);
                z1.input.addRecalcActions(() ->
                {
                    if (z1.input.valid())
                    {
                        objectiveEnterArea.coords[2].set(FilterInt.INSTANCE.parse(z1.input.text));
                        current.setObjective(objectiveEnterArea);
                    }
                });
                objectiveEditor.add(z1);
                objectiveEditor.add(new GUITextSpacer(this));

                GUILabeledTextInput x2 = new GUILabeledTextInput(this, "X2: ", "" + objectiveEnterArea.coords[3].value, FilterInt.INSTANCE);
                x2.input.addRecalcActions(() ->
                {
                    if (x2.input.valid())
                    {
                        objectiveEnterArea.coords[3].set(FilterInt.INSTANCE.parse(x2.input.text));
                        current.setObjective(objectiveEnterArea);
                    }
                });
                objectiveEditor.add(x2);
                objectiveEditor.add(new GUITextSpacer(this));

                GUILabeledTextInput y2 = new GUILabeledTextInput(this, "Y2: ", "" + objectiveEnterArea.coords[4].value, FilterInt.INSTANCE);
                y2.input.addRecalcActions(() ->
                {
                    if (y2.input.valid())
                    {
                        objectiveEnterArea.coords[4].set(FilterInt.INSTANCE.parse(y2.input.text));
                        current.setObjective(objectiveEnterArea);
                    }
                });
                objectiveEditor.add(y2);
                objectiveEditor.add(new GUITextSpacer(this));

                GUILabeledTextInput z2 = new GUILabeledTextInput(this, "Z2: ", "" + objectiveEnterArea.coords[5].value, FilterInt.INSTANCE);
                z2.input.addRecalcActions(() ->
                {
                    if (z2.input.valid())
                    {
                        objectiveEnterArea.coords[5].set(FilterInt.INSTANCE.parse(z2.input.text));
                        current.setObjective(objectiveEnterArea);
                    }
                });
                objectiveEditor.add(z2);
                objectiveEditor.add(new GUITextSpacer(this));
            }
        }

        objectiveEditor.add(new GUITextSpacer(this));

        currentView.recalc(0);
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
                    //Objectives were empty, but no longer are
                    conditions.add(new GUITextSpacer(this));
                    conditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newCondition != null) activeConditionElement.setCondition((CCondition) newCondition.copy());
            else
            {
                //Removing an objective
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

        objectiveEditor.recalc(0);
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

        objectiveEditor.recalc(0);
    }
}

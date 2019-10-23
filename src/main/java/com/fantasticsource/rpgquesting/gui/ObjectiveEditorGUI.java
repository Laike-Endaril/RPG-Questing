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
    private GUIText originalLabel, currentLabel, objectiveSelectorLabel, objectiveEditorLabel;
    private GUIGradientBorder[] separators = new GUIGradientBorder[4];
    private GUIScrollView objectiveSelector, objectiveEditor, originalView, currentView;
    private GUIVerticalScrollbar objectiveSelectorScrollbar, objectiveEditorScrollbar, originalScrollbar, currentScrollbar;
    private GUIAutocroppedView conditions;

    public ObjectiveEditorGUI(GUIObjective clickedElement)
    {
        this(clickedElement, 1);
    }

    public ObjectiveEditorGUI(GUIObjective clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.objective;
        selection = original == null ? null : (CObjective) original.copy();


        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));


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


        separators[0] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[0]);


        //Labels
        originalLabel = new GUIText(this, 0, 0, "ORIGINAL", Color.YELLOW.copy().setVF(0.2f));
        root.add(originalLabel);
        currentLabel = new GUIText(this, 0, 0, "CURRENT", Color.YELLOW.copy().setVF(0.2f));
        root.add(currentLabel);
        objectiveSelectorLabel = new GUIText(this, 0, 0, "OBJECTIVE SELECTION", Color.YELLOW.copy().setVF(0.2f));
        root.add(objectiveSelectorLabel);
        objectiveEditorLabel = new GUIText(this, 0, 0, "OBJECTIVE EDITING", Color.YELLOW.copy().setVF(0.2f));
        root.add(objectiveEditorLabel);


        //Original
        GUITextSpacer spacer = new GUITextSpacer(this, oneThird, true);
        originalView = new GUIScrollView(this, 0.48 - spacer.width * 2, oneThird);
        root.add(spacer.addRecalcActions(() -> originalView.width = 0.48 - spacer.width * 2));
        root.add(originalView);

        GUITextSpacer spacer2 = new GUITextSpacer(this, oneThird, true);
        root.add(spacer2);
        originalScrollbar = new GUIVerticalScrollbar(this, 0.02, oneThird, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, originalView);
        root.add(originalScrollbar);

        originalView.add(new GUITextSpacer(this));
        GUIObjective originalElement = new GUIObjective(this, current.objective == null ? null : (CObjective) current.objective.copy());
        originalView.add(originalElement.addClickActions(() -> setCurrent(originalElement.objective)));
        originalView.add(new GUITextSpacer(this));


        //Current
        GUITextSpacer spacer3 = new GUITextSpacer(this, oneThird, true);
        currentView = new GUIScrollView(this, 0.48 - spacer3.width * 2, oneThird);
        root.add(spacer3.addRecalcActions(() -> currentView.width = 0.48 - spacer3.width * 2));
        root.add(currentView);

        GUITextSpacer spacer4 = new GUITextSpacer(this, oneThird, true);
        root.add(spacer4);
        currentScrollbar = new GUIVerticalScrollbar(this, 0.02, oneThird, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, currentView);
        root.add(currentScrollbar);

        currentView.add(new GUITextSpacer(this));
        currentView.add(current);
        currentView.add(new GUITextSpacer(this));


        separators[2] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[2]);


        //Objective selector
        GUITextSpacer spacer5 = new GUITextSpacer(this, oneThird, true);
        objectiveSelector = new GUIScrollView(this, 0.98 - spacer5.width * 2, oneThird);
        root.add(spacer5.addRecalcActions(() -> objectiveSelector.width = 0.98 - spacer5.width * 2));
        root.add(objectiveSelector);

        GUITextSpacer spacer6 = new GUITextSpacer(this, oneThird, true);
        root.add(spacer6);
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


        separators[3] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[3]);


        //Objective editor
        GUITextSpacer spacer7 = new GUITextSpacer(this, oneThird, true);
        objectiveEditor = new GUIScrollView(this, 0.98 - spacer7.width * 2, oneThird);
        root.add(spacer7.addRecalcActions(() -> objectiveEditor.width = 0.98 - spacer7.width * 2));
        root.add(objectiveEditor);

        GUITextSpacer spacer8 = new GUITextSpacer(this, oneThird, true);
        root.add(spacer8);
        objectiveEditorScrollbar = new GUIVerticalScrollbar(this, 0.02, oneThird, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectiveEditor);
        root.add(objectiveEditorScrollbar);

        setCurrent(current.objective);


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;

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
            originalView.height = oneThirdHeight;
            spacer2.height = oneThirdHeight;
            originalScrollbar.height = oneThirdHeight;

            spacer3.height = oneThirdHeight;
            currentView.height = oneThirdHeight;
            spacer4.height = oneThirdHeight;
            currentScrollbar.height = oneThirdHeight;

            spacer5.height = oneThirdHeight;
            objectiveSelector.height = oneThirdHeight;
            spacer6.height = oneThirdHeight;
            objectiveSelectorScrollbar.height = oneThirdHeight;

            spacer7.height = oneThirdHeight;
            objectiveEditor.height = oneThirdHeight;
            spacer8.height = oneThirdHeight;
            objectiveEditorScrollbar.height = oneThirdHeight;
        });
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;

        objectiveSelectorLabel.x = objectiveSelector.x + objectiveSelector.width / 2 - objectiveSelectorLabel.width / 2;
        objectiveSelectorLabel.y = objectiveSelector.y + objectiveSelector.height / 2 - objectiveSelectorLabel.height / 2;

        objectiveEditorLabel.x = objectiveEditor.x + objectiveEditor.width / 2 - objectiveEditorLabel.width / 2;
        objectiveEditorLabel.y = objectiveEditor.y + objectiveEditor.height / 2 - objectiveEditorLabel.height / 2;


        root.recalc(0);
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

                GUILabeledTextInput quantity = new GUILabeledTextInput(this, "Quantity: ", "" + objectiveKill.required.value, FilterInt.INSTANCE);
                quantity.input.addRecalcActions(() ->
                {
                    if (quantity.input.valid())
                    {
                        objectiveKill.required.set(FilterInt.INSTANCE.parse(quantity.input.text));
                        current.setObjective(objectiveKill);
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
                objectiveEditor.add(stackToMatch.addClickActions(() ->
                {
                    ItemSelectionGUI gui = new ItemSelectionGUI(stackToMatch, textScale);
                    gui.addOnClosedActions(() ->
                    {
                        stackToMatch.setStack(gui.selection);
                        objectiveCollect.stackToMatch.set(gui.selection);
                        current.setObjective(objectiveCollect);
                        objectiveEditor.recalc(0);
                    });
                }));
                objectiveEditor.add(new GUITextSpacer(this));
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

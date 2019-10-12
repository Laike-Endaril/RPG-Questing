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
import com.fantasticsource.mctools.gui.element.text.filter.FilterBoolean;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.screen.ItemSelectionGUI;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.rpgquesting.quest.objective.CObjectiveCollect;
import com.fantasticsource.rpgquesting.quest.objective.CObjectiveKill;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class BranchEditorGUI extends GUIScreen
{
    public CDialogueBranch original, selection;
    public GUIObjective current;
    private GUITextButton delete;
    private GUIText originalLabel, currentLabel, objectiveSelectorLabel, objectiveEditorLabel;
    private GUIGradientBorder[] separators = new GUIGradientBorder[4];
    private GUIScrollView objectiveSelector, objectiveEditor, originalView, currentView;
    private GUIVerticalScrollbar objectiveSelectorScrollbar, objectiveEditorScrollbar, originalScrollbar, currentScrollbar;
    private GUIAutocroppedView conditions;

    public BranchEditorGUI(GUIBranch clickedElement)
    {
//        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
//        else Minecraft.getMinecraft().displayGuiScreen(this);
//
//
//        drawStack = false;
//
//
//        original = clickedElement.objective;
//        selection = original == null ? null : (CObjective) original.copy();
//
//
//        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));
//
//
//        //Management
//        current = new GUIObjective(this, selection);
//        GUITextButton save = new GUITextButton(this, "Save and Close", GREEN[0]);
//        root.add(save.addClickActions(() ->
//        {
//            selection = current.objective;
//            close();
//        }));
//
//        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
//        root.add(cancel.addClickActions(() ->
//        {
//            selection = original;
//            close();
//        }));
//
//        delete = new GUITextButton(this, "Delete Objective and Close", RED[0]);
//        root.add(delete.addClickActions(() ->
//        {
//            selection = null;
//            close();
//        }));
//
//
//        double free = 1 - delete.height - 0.03;
//
//
//        separators[0] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
//        root.add(separators[0]);
//
//
//        //Labels
//        originalLabel = new GUIText(this, 0, 0, "ORIGINAL", Color.YELLOW.copy().setVF(0.2f));
//        root.add(originalLabel);
//        currentLabel = new GUIText(this, 0, 0, "CURRENT", Color.YELLOW.copy().setVF(0.2f));
//        root.add(currentLabel);
//        objectiveSelectorLabel = new GUIText(this, 0, 0, "OBJECTIVE SELECTION", Color.YELLOW.copy().setVF(0.2f));
//        root.add(objectiveSelectorLabel);
//        objectiveEditorLabel = new GUIText(this, 0, 0, "OBJECTIVE EDITING", Color.YELLOW.copy().setVF(0.2f));
//        root.add(objectiveEditorLabel);
//
//
//        //Original
//        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
//        originalView = new GUIScrollView(this, 0.44, free / 3);
//        root.add(originalView);
//        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
//        originalScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, originalView);
//        root.add(originalScrollbar);
//
//        originalView.add(new GUIText(this, "\n"));
//        GUIObjective originalElement = new GUIObjective(this, current.objective == null ? null : (CObjective) current.objective.copy());
//        originalView.add(originalElement.addClickActions(() -> setCurrent(originalElement.objective)));
//        originalView.add(new GUIText(this, "\n"));
//
//
//        //Current
//        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
//        currentView = new GUIScrollView(this, 0.44, free / 3);
//        root.add(currentView);
//        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
//        currentScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, currentView);
//        root.add(currentScrollbar);
//
//        currentView.add(new GUIText(this, "\n"));
//        currentView.add(current);
//        currentView.add(new GUIText(this, "\n"));
//
//
//        separators[2] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
//        root.add(separators[2]);
//
//
//        //Objective selector
//        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
//        objectiveSelector = new GUIScrollView(this, 0.94, free / 3);
//        root.add(objectiveSelector);
//        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
//        objectiveSelectorScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectiveSelector);
//        root.add(objectiveSelectorScrollbar);
//
//        //Objective types
//        objectiveSelector.add(new GUIText(this, "\n"));
//
//        objectiveSelector.add(new CObjectiveKill().getChoosableElement(this));
//        objectiveSelector.add(new GUIText(this, "\n"));
//
//        objectiveSelector.add(new CObjectiveCollect().getChoosableElement(this));
//        objectiveSelector.add(new GUIText(this, "\n"));
//
//
//        for (int i = objectiveSelector.size() - 1; i >= 0; i--)
//        {
//            GUIElement element = objectiveSelector.get(i);
//            if (element instanceof GUIObjective)
//            {
//                element.addClickActions(() -> setCurrent(((GUIObjective) element).objective));
//            }
//        }
//
//
//        separators[3] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
//        root.add(separators[3]);
//
//
//        //Objective editor
//        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
//        objectiveEditor = new GUIScrollView(this, 0.94, free / 3);
//        root.add(objectiveEditor);
//        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
//
//        objectiveEditorScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, objectiveEditor);
//        root.add(objectiveEditorScrollbar);
//
//        setCurrent(current.objective);
//
//
//        //Reposition labels
//        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
//        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;
//
//        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
//        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;
//
//        objectiveSelectorLabel.x = objectiveSelector.x + objectiveSelector.width / 2 - objectiveSelectorLabel.width / 2;
//        objectiveSelectorLabel.y = objectiveSelector.y + objectiveSelector.height / 2 - objectiveSelectorLabel.height / 2;
//
//        objectiveEditorLabel.x = objectiveEditor.x + objectiveEditor.width / 2 - objectiveEditorLabel.width / 2;
//        objectiveEditorLabel.y = objectiveEditor.y + objectiveEditor.height / 2 - objectiveEditorLabel.height / 2;
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

        objectiveSelector.height = free / 3;
        objectiveSelectorScrollbar.height = free / 3;

        objectiveEditor.height = free / 3;
        objectiveEditorScrollbar.height = free / 3;


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;

        objectiveSelectorLabel.x = objectiveSelector.x + objectiveSelector.width / 2 - objectiveSelectorLabel.width / 2;
        objectiveSelectorLabel.y = objectiveSelector.y + objectiveSelector.height / 2 - objectiveSelectorLabel.height / 2;

        objectiveEditorLabel.x = objectiveEditor.x + objectiveEditor.width / 2 - objectiveEditorLabel.width / 2;
        objectiveEditorLabel.y = objectiveEditor.y + objectiveEditor.height / 2 - objectiveEditorLabel.height / 2;


        root.recalc();
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CObjective objective)
    {
        current.setObjective(objective);


        objectiveEditor.clear();

        objectiveEditor.add(new GUIText(this, "\n"));

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
            objectiveEditor.add(new GUIText(this, "\n"));

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
            objectiveEditor.add(new GUIText(this, "\n"));

            Class cls = objective.getClass();
            if (cls == CObjectiveKill.class)
            {
                objectiveEditor.add(new GUIText(this, "\n"));

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
                objectiveEditor.add(new GUIText(this, "\n"));


                objectiveEditor.add(new GUIText(this, "\n" + TextFormatting.GOLD + "Conditions...\n"));
                conditions = new GUIAutocroppedView(this);
                objectiveEditor.add(conditions);

                for (CCondition condition : objectiveKill.conditions)
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

                if (objectiveKill.conditions.size() > 0)
                {
                    conditions.add(new GUIText(this, "\n"));
                    conditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearConditions));
                }
            }
            else if (cls == CObjectiveCollect.class)
            {
                CObjectiveCollect objectiveCollect = (CObjectiveCollect) objective;
                GUIItemStack stackToMatch = new GUIItemStack(this, objectiveCollect.stackToMatch.stack);
                objectiveEditor.add(stackToMatch.addClickActions(() ->
                {
                    ItemSelectionGUI gui = new ItemSelectionGUI(stackToMatch);
                    gui.addOnClosedActions(() ->
                    {
                        stackToMatch.setStack(gui.selection);
                        objectiveCollect.stackToMatch.set(gui.selection);
                        current.setObjective(objectiveCollect);
                        objectiveEditor.recalc();
                    });
                }));
                objectiveEditor.add(new GUIText(this, "\n"));
            }
        }

        objectiveEditor.add(new GUIText(this, "\n"));

        currentView.recalc();
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
                    conditions.add(new GUIText(this, "\n"));
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

        objectiveEditor.recalc();
    }

    private void clearConditions()
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

        objectiveEditor.recalc();
    }
}

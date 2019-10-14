package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogueChoice;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class ChoiceEditorGUI extends GUIScreen
{
    public CDialogueChoice original, selection;
    public GUIChoice current;
    private GUITextButton delete;
    private GUIGradientBorder[] separators = new GUIGradientBorder[4];
    private GUIScrollView choiceSelector, choiceEditor, originalView, currentView;
    private GUIVerticalScrollbar choiceSelectorScrollbar, choiceEditorScrollbar, originalScrollbar, currentScrollbar;
    private GUIAutocroppedView conditions;

    public ChoiceEditorGUI(GUIChoice clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.choice;
        selection = original == null ? null : (CDialogueChoice) original.copy();


        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));


        //Management
        current = new GUIChoice(this, selection);
        GUITextButton save = new GUITextButton(this, "Save and Close", GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            selection = current.choice;
            close();
        }));

        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel.addClickActions(() ->
        {
            selection = original;
            close();
        }));

        delete = new GUITextButton(this, "Delete Choice and Close", RED[0]);
        root.add(delete.addClickActions(() ->
        {
            selection = null;
            close();
        }));


        double free = 1 - delete.height - 0.03;


        separators[0] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[0]);


        //Original
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalView = new GUIScrollView(this, 0.44, free / 3);
        root.add(originalView);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, originalView);
        root.add(originalScrollbar);

        originalView.add(new GUIText(this, "\n"));
        GUIChoice originalElement = new GUIChoice(this, current.choice == null ? null : (CDialogueChoice) current.choice.copy());
        originalView.add(originalElement.addClickActions(() -> setCurrent(originalElement.choice)));
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


        //Choice selector
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        choiceSelector = new GUIScrollView(this, 0.94, free / 3);
        root.add(choiceSelector);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        choiceSelectorScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, choiceSelector);
        root.add(choiceSelectorScrollbar);

        //Choice types
        choiceSelector.add(new GUIText(this, "\n"));

//        choiceSelector.add(new CChoiceKill().getChoosableElement(this));
//        choiceSelector.add(new GUIText(this, "\n"));

//        choiceSelector.add(new CChoiceCollect().getChoosableElement(this));
//        choiceSelector.add(new GUIText(this, "\n"));


        for (int i = choiceSelector.size() - 1; i >= 0; i--)
        {
            GUIElement element = choiceSelector.get(i);
            if (element instanceof GUIChoice)
            {
                element.addClickActions(() -> setCurrent(((GUIChoice) element).choice));
            }
        }


        separators[3] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[3]);


        //Choice editor
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        choiceEditor = new GUIScrollView(this, 0.94, free / 3);
        root.add(choiceEditor);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));

        choiceEditorScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, choiceEditor);
        root.add(choiceEditorScrollbar);

        setCurrent(current.choice);
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

        choiceSelector.height = free / 3;
        choiceSelectorScrollbar.height = free / 3;

        choiceEditor.height = free / 3;
        choiceEditorScrollbar.height = free / 3;


        root.recalc();
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CDialogueChoice choice)
    {
        current.setChoice(choice);


        choiceEditor.clear();

        choiceEditor.add(new GUIText(this, "\n"));

        if (choice != null)
        {
//            GUILabeledTextInput progressIsPrefix = new GUILabeledTextInput(this, "Progress is prefix: ", "" + choice.progressIsPrefix.value, FilterBoolean.INSTANCE);
//            progressIsPrefix.input.addRecalcActions(() ->
//            {
//                if (progressIsPrefix.input.valid())
//                {
//                    choice.progressIsPrefix.set(FilterBoolean.INSTANCE.parse(progressIsPrefix.input.text));
//                    current.setChoice(choice);
//                }
//            });
//            choiceEditor.add(progressIsPrefix);
            choiceEditor.add(new GUIText(this, "\n"));

            GUILabeledTextInput text = new GUILabeledTextInput(this, "Text: ", choice.text.value, FilterNotEmpty.INSTANCE);
            text.input.addRecalcActions(() ->
            {
                if (text.input.valid())
                {
                    choice.text.set(FilterNotEmpty.INSTANCE.parse(text.input.text));
                    current.setChoice(choice);
                }
            });
            choiceEditor.add(text);
            choiceEditor.add(new GUIText(this, "\n"));
        }

        choiceEditor.add(new GUIText(this, "\n"));

        currentView.recalc();
    }

    private void editCondition(GUICondition activeConditionElement, CCondition newCondition)
    {
        if (activeConditionElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
        {
            //Started with empty slot
            if (newCondition != null)
            {
                //Added new choice
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
                    //Choices were empty, but no longer are
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
                //Removing a choice
                int index = conditions.indexOf(activeConditionElement);
                conditions.remove(index);
                conditions.remove(index);

                if (conditions.size() == 5)
                {
                    //Had one choice, and now have 0 (remove the "clear all" option)
                    conditions.remove(3);
                    conditions.remove(3);
                }
            }
        }

        choiceEditor.recalc();
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

        choiceEditor.recalc();
    }
}

package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIMultilineTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Colors;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogueChoice;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class BranchEditorGUI extends GUIScreen
{
    public CDialogueBranch original, selection;
    private GUIGradientBorder separator;
    private GUITabView tabView;
    private GUIScrollView paragraphView, choicesView;
    private GUIMultilineTextInput paragraph;

    public BranchEditorGUI(GUIBranch clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.branch;
        selection = original == null ? null : (CDialogueBranch) original.copy();


        //Paragraph tab
        paragraph = new GUIMultilineTextInput(this, original == null ? "(Replace this with dialogue text)" : original.paragraph.value, FilterNone.INSTANCE);
        paragraphView.add(paragraph.addRecalcActions(() ->
        {
            paragraphView.recalcThisOnly();


            //Focus on current line
            //TODO change this code to account for other elements in the view, then add spacers above and below the paragraph
            int line = paragraph.cursorLine();
            double ratio = 1d / paragraph.fullLineCount();
            double lineTop = line * ratio * paragraphView.internalHeight;
            double lineBottom = (line + 1) * ratio * paragraphView.internalHeight;
            if (lineTop < paragraphView.top) paragraphView.progress = lineTop / (paragraphView.internalHeight - 1);
            else if (lineBottom > paragraphView.bottom) paragraphView.progress = (lineBottom - 1) / (paragraphView.internalHeight - 1);
        }));


        //Choices tab
        choicesView.clear();

        if (original != null)
        {
            for (CDialogueChoice choice : original.choices)
            {
                choicesView.add(new GUITextSpacer(this));
                GUIChoice choiceElement = new GUIChoice(this, choice);
                choicesView.add(choiceElement.addClickActions(() ->
                {
                    ChoiceEditorGUI gui = new ChoiceEditorGUI(choiceElement, textScale);
                    gui.addOnClosedActions(() -> editChoice(choiceElement, gui.selection));
                }));
            }
        }

        {
            choicesView.add(new GUITextSpacer(this));
            GUIChoice choiceElement = new GUIChoice(this, null);
            choiceElement.text = TextFormatting.DARK_PURPLE + "(Add new choice)";
            choicesView.add(choiceElement.addClickActions(() ->
            {
                ChoiceEditorGUI gui = new ChoiceEditorGUI(choiceElement, textScale);
                gui.addOnClosedActions(() -> editChoice(choiceElement, gui.selection));
            }));
        }

        if (original != null && original.choices.size() > 0)
        {
            choicesView.add(new GUITextSpacer(this));
            choicesView.add(new GUIText(this, "(Clear all choices)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearChoices));
        }

        choicesView.add(new GUITextSpacer(this));
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
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));


        //Management
        root.add(new GUITextButton(this, "Save and Close", GREEN[0]).addClickActions(() ->
        {
            selection = new CDialogueBranch(paragraph.text);

            //Dummy value to prevent errors.  This is re-set to the correct value in DialogueEditorGUI.trySave()
            selection.dialogueName.set("");

            for (GUIElement element : choicesView.children)
            {
                if (element instanceof GUIChoice)
                {
                    CDialogueChoice choice = ((GUIChoice) element).choice;
                    if (choice != null) selection.add(choice);
                }
            }

            close();
        }));

        root.add(new GUITextButton(this, "Close Without Saving").addClickActions(() ->
        {
            selection = original;
            close();
        }));

        root.add(new GUITextButton(this, "Delete Branch and Close", RED[0]).addClickActions(() ->
        {
            selection = null;
            close();
        }));


        separator = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separator);


        tabView = new GUITabView(this, 0, separator.y + separator.height, 1, 1 - separator.y - separator.height, "Paragraph", "Choices");
        root.add(tabView);


        //Paragraph tab
        GUITextSpacer spacer = new GUITextSpacer(this, true);
        paragraphView = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
        tabView.tabViews.get(0).add(spacer.addRecalcActions(() -> paragraphView.width = 0.98 - spacer.width * 2));
        tabView.tabViews.get(0).add(paragraphView);

        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, paragraphView));

        tabView.tabs.get(0).addClickActions(() -> paragraph.setActive(true));


        //Choices tab
        GUITextSpacer spacer2 = new GUITextSpacer(this, true);
        choicesView = new GUIScrollView(this, 0.98 - spacer2.width * 2, 1);
        tabView.tabViews.get(1).add(spacer2.addRecalcActions(() -> choicesView.width = 0.98 - spacer2.width * 2));
        tabView.tabViews.get(1).add(choicesView);

        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, choicesView));
    }


    private void editChoice(GUIChoice activeChoiceElement, CDialogueChoice newChoice)
    {
        if (activeChoiceElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new choice)"))
        {
            //Started with empty slot
            if (newChoice != null)
            {
                //Added new choice
                int index = choicesView.indexOf(activeChoiceElement);

                {
                    choicesView.add(index, new GUITextSpacer(this));
                    GUIChoice choiceElement = new GUIChoice(this, (CDialogueChoice) newChoice.copy());
                    choicesView.add(index, choiceElement.addClickActions(() ->
                    {
                        ChoiceEditorGUI gui = new ChoiceEditorGUI(choiceElement, textScale);
                        gui.addOnClosedActions(() -> editChoice(choiceElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Choices were empty, but no longer are
                    choicesView.add(new GUIText(this, "(Clear all choices)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearChoices));
                    choicesView.add(new GUITextSpacer(this));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newChoice != null) activeChoiceElement.setChoice((CDialogueChoice) newChoice.copy());
            else
            {
                //Removing a choice
                int index = choicesView.indexOf(activeChoiceElement);
                choicesView.remove(index);
                choicesView.remove(index);

                if (choicesView.size() == 5)
                {
                    //Had one choice, and now have 0 (remove the "clear all" option)
                    choicesView.remove(3);
                    choicesView.remove(3);
                }
            }
        }
    }

    private void clearChoices()
    {
        choicesView.clear();

        choicesView.add(new GUITextSpacer(this));
        GUIChoice choiceElement = new GUIChoice(this, null);
        choiceElement.text = TextFormatting.DARK_PURPLE + "(Add new choice)";
        choicesView.add(choiceElement.addClickActions(() ->
        {
            ChoiceEditorGUI gui = new ChoiceEditorGUI(choiceElement, textScale);
            gui.addOnClosedActions(() -> editChoice(choiceElement, gui.selection));
        }));
        choicesView.add(new GUITextSpacer(this));

        tabView.recalc(0);
    }
}

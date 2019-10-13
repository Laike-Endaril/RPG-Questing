package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIMultilineTextInput;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class BranchEditorGUI extends GUIScreen
{
    public CDialogueBranch original, selection;
    private GUIGradientBorder separator;
    private GUITabView tabView;
    private GUIScrollView paragraphView, choicesView;
    private GUIMultilineTextInput paragraph;

    public BranchEditorGUI(GUIBranch clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.branch;
        selection = (CDialogueBranch) original.copy();


        //Paragraph tab
        paragraph = new GUIMultilineTextInput(this, original.paragraph.value, FilterNone.INSTANCE);
        paragraphView.add(paragraph.addRecalcActions(() ->
        {
            paragraphView.recalcThisOnly();


            int line = paragraph.cursorLine();
            double ratio = 1d / paragraph.fullLineCount();
            double lineTop = line * ratio * paragraphView.internalHeight;
            double lineBottom = (line + 1) * ratio * paragraphView.internalHeight;
            if (lineTop < paragraphView.top) paragraphView.progress = lineTop / (paragraphView.internalHeight - 1);
            else if (lineBottom > paragraphView.bottom) paragraphView.progress = (lineBottom - 1) / (paragraphView.internalHeight - 1);
        }));


        //Choices tab
        choicesView.clear();
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);

        //Resize views and scrollbars
        tabView.y = separator.y + separator.height;
        tabView.height = 1 - tabView.y;


        root.recalc();
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        //Management
        root.add(new GUITextButton(this, "Save and Close", GREEN[0]).addClickActions(() ->
        {
            selection = new CDialogueBranch(paragraph.text);
            selection.dialogueName.set(original.dialogueName.value);
            //TODO add new choices to selection
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
        paragraphView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(0).add(paragraphView);
        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, paragraphView));


        //Choices tab
        choicesView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(1).add(choicesView);
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, choicesView));
    }
}
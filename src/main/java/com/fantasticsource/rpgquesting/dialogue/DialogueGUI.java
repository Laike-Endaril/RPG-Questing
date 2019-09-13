package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class DialogueGUI extends GUIScreen
{
    public static DialogueGUI GUI;
    private static GUIScrollView scrollView;
    private static GUIVerticalScrollbar scrollbar;
    private static GUIGradientBorder buttonBar;

    static
    {
        GUI = new DialogueGUI();
    }

    private DialogueGUI()
    {
    }

    public static void show(CDialogue dialogue)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);
        double buttonHeight = new GUITextButton(this, "A").recalc().height;

        scrollView.height = 1 - buttonHeight;
        scrollView.recalc();
        GUIVerticalScrollbar oldScrollbar = scrollbar;
        scrollbar = new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1 - buttonHeight, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView);
        guiElements.set(guiElements.indexOf(oldScrollbar), scrollbar);

        buttonBar.y = 1 - buttonHeight;
        buttonBar.height = buttonHeight;
        buttonBar.recalc();
    }

    @Override
    protected void init()
    {
        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        double buttonHeight = new GUITextButton(this, "A").height;

        scrollView = new GUIScrollView(this, 0, 0, 0.98, 1 - buttonHeight);
        guiElements.add(scrollView);
        scrollbar = new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1 - buttonHeight, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView);
        guiElements.add(scrollbar);

        buttonBar = new GUIGradientBorder(this, 0, 1 - buttonHeight, 1, buttonHeight, 0.2, Color.GRAY, Color.BLANK);
        guiElements.add(buttonBar);

        //TODO temp code past this point
        scrollView.add(new GUIText(this, "Blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah", Color.WHITE, Color.RED, Color.BLUE));
        scrollView.add(new GUIText(this, "Blah blah", Color.WHITE, Color.RED, Color.BLUE));
        scrollView.add(new GUIText(this, "Blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah", Color.WHITE, Color.RED, Color.BLUE));
        scrollView.add(new GUIText(this, "Blah blah", Color.WHITE, Color.RED, Color.BLUE));
        scrollView.add(new GUIText(this, "Blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah", Color.WHITE, Color.RED, Color.BLUE));

        buttonBar.add(new GUITextButton(this, "Accept"));
        buttonBar.add(new GUITextButton(this, "Decline"));
    }
}

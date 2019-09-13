package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class DialogueGUI extends GUIScreen
{
    public static DialogueGUI GUI;

    private static GUIScrollView scrollView;

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

        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> options = new ArrayList<>();

        scrollView.clear();
        for (String line : lines) scrollView.add(new GUIText(GUI, processString(line)));
        scrollView.add(new GUIText(GUI, ""));
        for (String option : options) scrollView.add(new GUIText(GUI, processString(option)));
    }

    @Override
    protected void init()
    {
        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        scrollView = new GUIScrollView(this, 0, 0, 0.98, 1);
        guiElements.add(scrollView);
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView);
        guiElements.add(scrollbar);
    }

    public static String processString(String string)
    {
        return string.replaceAll("@p", Minecraft.getMinecraft().player.getName());
    }
}

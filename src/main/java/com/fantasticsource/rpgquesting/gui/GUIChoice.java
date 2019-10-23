package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.rpgquesting.dialogue.CDialogueChoice;

public class GUIChoice extends GUIText
{
    CDialogueChoice choice;


    public GUIChoice(GUIScreen screen, CDialogueChoice choice)
    {
        this(screen, choice, 1);
    }

    public GUIChoice(GUIScreen screen, CDialogueChoice choice, double scale)
    {
        super(screen, choice == null ? "(Empty Slot)" : choice.text.value, scale);

        this.choice = choice;
    }


    public GUIChoice(GUIScreen screen, double x, double y, CDialogueChoice choice)
    {
        this(screen, x, y, choice, 1);
    }

    public GUIChoice(GUIScreen screen, double x, double y, CDialogueChoice choice, double scale)
    {
        super(screen, x, y, choice == null ? "(Empty Slot)" : choice.text.value, scale);

        this.choice = choice;
    }


    public GUIChoice setChoice(CDialogueChoice choice)
    {
        text = choice == null ? "(Empty Slot)" : choice.text.value;

        this.choice = choice;

        recalc(0);

        return this;
    }
}

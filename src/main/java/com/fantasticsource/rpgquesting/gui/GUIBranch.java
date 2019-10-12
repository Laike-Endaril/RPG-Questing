package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;

public class GUIBranch extends GUIText
{
    CDialogueBranch branch;

    public GUIBranch(GUIScreen screen, CDialogueBranch branch, String text)
    {
        super(screen, text);

        this.branch = branch;
    }

    public GUIBranch(GUIScreen screen, double x, double y, CDialogueBranch branch, String text)
    {
        super(screen, x, y, text);

        this.branch = branch;
    }


    public void setBranch(CDialogueBranch branch)
    {
        this.branch = branch;
    }

    public GUIBranch setBranch(CDialogueBranch branch, String text)
    {
        this.text = text;

        this.branch = branch;

        recalc();

        return this;
    }
}

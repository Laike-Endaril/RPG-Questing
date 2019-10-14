package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.rpgquesting.actions.CAction;

import java.util.ArrayList;

public class GUIAction extends GUIText
{
    CAction action;

    public GUIAction(GUIScreen screen, CAction action)
    {
        super(screen, action == null ? "(Empty Slot)" : process(action.description()));

        this.action = action;
    }

    public GUIAction(GUIScreen screen, double x, double y, CAction action)
    {
        super(screen, x, y, action == null ? "(Empty Slot)" : process(action.description()));

        this.action = action;
    }

    private static String process(ArrayList<String> input)
    {
        StringBuilder result = new StringBuilder(input.get(0));

        for (int i = 1; i < input.size(); i++)
        {
            result.append("\n").append(input.get(i));
        }

        return result.toString();
    }

    public GUIAction setAction(CAction action)
    {
        text = action == null ? "(Empty Slot)" : process(action.description());

        this.action = action;

        recalc();

        return this;
    }
}

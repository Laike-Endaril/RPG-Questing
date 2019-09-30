package com.fantasticsource.rpgquesting.conditions.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.rpgquesting.conditions.CCondition;

import java.util.ArrayList;

public class GUICondition extends GUIText
{
    CCondition condition;

    public GUICondition(GUIScreen screen, CCondition condition)
    {
        super(screen, condition == null ? "(Empty Slot)" : process(condition.description()));

        this.condition = condition;
    }

    public GUICondition(GUIScreen screen, double x, double y, CCondition condition)
    {
        super(screen, x, y, condition == null ? "(Empty Slot)" : process(condition.description()));

        this.condition = condition;
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

    public GUICondition setCondition(CCondition condition)
    {
        text = condition == null ? "(Empty Slot)" : process(condition.description());

        this.condition = condition;

        return this;
    }
}

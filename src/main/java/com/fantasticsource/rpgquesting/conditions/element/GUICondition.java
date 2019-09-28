package com.fantasticsource.rpgquesting.conditions.element;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.rpgquesting.conditions.CCondition;

public class GUICondition extends GUIView
{
    CCondition condition;

    public GUICondition(GUIScreen screen, CCondition condition)
    {
        super(screen, 1, 1);

        this.condition = condition;
    }

    public GUICondition(GUIScreen screen, double x, double y, CCondition condition)
    {
        super(screen, x, y, 1, 1);

        this.condition = condition;
    }
}

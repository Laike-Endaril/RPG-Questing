package com.fantasticsource.rpgquesting.conditions.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import net.minecraft.util.text.TextFormatting;

public class GUICondition extends GUIText
{
    CCondition condition;

    public GUICondition(GUIScreen screen, CCondition condition)
    {
        super(screen, condition == null ? TextFormatting.GOLD + "(Empty Slot)" : condition.description().replace("Requires", "Require"));

        this.condition = condition;
    }

    public GUICondition(GUIScreen screen, double x, double y, CCondition condition)
    {
        super(screen, x, y, condition == null ? TextFormatting.GOLD + "(Empty Slot)" : condition.description().replace("Requires", "Require"));

        this.condition = condition;
    }

    public GUICondition setCondition(CCondition condition)
    {
        text = condition == null ? TextFormatting.GOLD + "(Empty Slot)" : condition.description().replace("Requires", "Require");

        this.condition = condition;

        return this;
    }
}

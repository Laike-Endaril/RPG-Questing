package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;

public class GUIObjective extends GUIText
{
    CObjective objective;


    public GUIObjective(GUIScreen screen, CObjective objective)
    {
        this(screen, objective, 1);
    }

    public GUIObjective(GUIScreen screen, CObjective objective, double scale)
    {
        super(screen, objective == null ? "(Empty Slot)" : objective.getFullText(), scale);

        this.objective = objective;
    }


    public GUIObjective(GUIScreen screen, double x, double y, CObjective objective)
    {
        this(screen, x, y, objective, 1);
    }

    public GUIObjective(GUIScreen screen, double x, double y, CObjective objective, double scale)
    {
        super(screen, x, y, objective == null ? "(Empty Slot)" : objective.getFullText(), scale);

        this.objective = objective;
    }


    public GUIObjective setObjective(CObjective objective)
    {
        text = objective == null ? "(Empty Slot)" : objective.getFullText();

        this.objective = objective;

        recalc();

        return this;
    }
}

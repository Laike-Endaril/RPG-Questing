package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.rpgquesting.selectionguis.GUICondition;
import com.fantasticsource.tools.component.Component;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public abstract class CCondition extends Component
{
    public abstract ArrayList<String> unmetConditions(Entity entity);

    public abstract String description();

    public abstract GUICondition getChoosableElement(GUIScreen screen);

    public abstract GUIElement getEditableElement(GUIScreen screen);
}

package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.conditions.gui.GUICondition;
import com.fantasticsource.tools.component.Component;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public abstract class CCondition extends Component
{
    public abstract ArrayList<String> unmetConditions(Entity entity);

    public abstract ArrayList<String> description();

    public abstract GUICondition getChoosableElement(GUIScreen screen);
}

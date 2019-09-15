package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.tools.component.Component;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public abstract class CCondition extends Component
{
    public abstract ArrayList<String> unmetConditions(Entity entity);
}

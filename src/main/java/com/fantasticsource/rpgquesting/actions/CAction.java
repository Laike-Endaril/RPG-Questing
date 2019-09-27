package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.conditions.CConditionAnd;
import com.fantasticsource.tools.component.Component;
import net.minecraft.entity.Entity;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;

public abstract class CAction extends Component
{
    public ArrayList<CCondition> conditions = new ArrayList<>();

    public CAction addConditions(CCondition... conditions)
    {
        this.conditions.addAll(Arrays.asList(conditions));
        return this;
    }

    public final ArrayList<String> tryExecute(Entity entity)
    {
        ArrayList<String> result = new CConditionAnd().add(conditions.toArray(new CCondition[0])).unmetConditions(entity);
        if (result.size() == 0) execute(entity);
        return result;
    }

    protected abstract void execute(Entity entity);

    public abstract String description();
}

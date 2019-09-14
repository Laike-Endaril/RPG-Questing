package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.tools.component.Component;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;

public abstract class CAction extends Component
{
    public ArrayList<CCondition> conditions = new ArrayList<>();

    public final void tryExecute(EntityPlayerMP player)
    {
        for (CCondition condition : conditions) if (!condition.check(player)) return;

        execute(player);
    }

    protected abstract void execute(EntityPlayerMP player);
}

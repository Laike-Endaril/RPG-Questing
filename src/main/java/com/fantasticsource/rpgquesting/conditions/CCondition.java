package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.tools.component.Component;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class CCondition extends Component
{
    public abstract boolean check(EntityPlayerMP player);
}

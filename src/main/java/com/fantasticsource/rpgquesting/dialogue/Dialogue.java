package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import net.minecraft.entity.Entity;

public class Dialogue extends Component
{
    public final CStringUTF8 name;

    public Dialogue(String name)
    {
        this.name = new CStringUTF8(this);
    }

    public boolean entityHas(Entity entity)
    {
        return false;
    }
}

package com.fantasticsource.rpgquesting.dialogue;

import net.minecraft.entity.Entity;

public class Dialogue
{
    public final String name;

    public Dialogue(String name)
    {
        this.name = name;
    }

    public boolean entityHas(Entity entity)
    {
        return false;
    }
}

package com.fantasticsource.rpgquesting.dialogue;

import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class Dialogues
{
    public static ArrayList<Dialogue> dialogues = new ArrayList<>();

    public static boolean handle(Entity entity)
    {
        ArrayList<Dialogue> found = new ArrayList<>();
        for (Dialogue dialogue : dialogues)
        {
            if (dialogue.entityHas(entity)) found.add(dialogue);
        }
        if (found.size() == 0) return false;

        if (entity.world.isRemote) DialogueGUI.IN
        return true;
    }
}

package com.fantasticsource.rpgquesting.dialogue;

import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class Dialogues
{
    public static ArrayList<CDialogue> dialogues = new ArrayList<>();

    public static boolean handle(Entity entity)
    {
        ArrayList<CDialogue> found = new ArrayList<>();
        for (CDialogue dialogue : dialogues)
        {
            if (dialogue.entityHas(entity)) found.add(dialogue);
        }
        if (found.size() == 0) return false;

        //Early exit if we're server-side
        if (!entity.world.isRemote) return true;

        if (found.size() == 1) DialogueGUI.show(found.get(0));
        else DialoguesGUI.show(found);
        return true;
    }
}

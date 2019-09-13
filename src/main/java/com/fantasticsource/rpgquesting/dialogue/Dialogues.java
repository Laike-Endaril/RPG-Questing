package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Dialogues
{
    public static LinkedHashMap<String, CDialogue> dialogues = new LinkedHashMap<>();

    public static boolean handle(EntityPlayerMP player, Entity entity)
    {
        ArrayList<CDialogue> found = new ArrayList<>();
        for (CDialogue dialogue : dialogues.values())
        {
            if (dialogue.entityHas(entity)) found.add(dialogue);
        }

        if (found.size() == 0) return false;
        else if (found.size() == 1)
        {
            Network.WRAPPER.sendTo(new DialogueBranchPacket(true, found.get(0).branches.get(0)), player);
        }
        else
        {
            //TODO multiple dialogues to choose from
        }

        return true;
    }
}

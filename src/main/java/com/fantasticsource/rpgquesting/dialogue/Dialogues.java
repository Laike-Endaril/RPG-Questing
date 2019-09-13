package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Dialogues
{
    private static LinkedHashMap<String, CDialogue> dialogues = new LinkedHashMap<>();

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
            Network.WRAPPER.sendTo(new Network.MultipleDialoguesPacket(entity.getEntityId(), found), player);
        }

        return true;
    }

    public static void add(CDialogue dialogue)
    {
        dialogues.put(dialogue.saveName.value, dialogue);
    }

    public static CDialogue get(String saveName)
    {
        return dialogues.get(saveName);
    }
}

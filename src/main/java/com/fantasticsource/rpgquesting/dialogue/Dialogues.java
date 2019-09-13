package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import com.fantasticsource.rpgquesting.Network.MultipleDialoguesPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class Dialogues
{
    public static int targetID = -1;

    private static LinkedHashMap<String, CDialogue> dialoguesBySaveName = new LinkedHashMap<>();
    private static LinkedHashMap<UUID, CDialogue> dialoguesByID = new LinkedHashMap<>();

    public static boolean handle(EntityPlayerMP player, Entity entity)
    {
        ArrayList<CDialogue> found = new ArrayList<>();
        for (CDialogue dialogue : dialoguesBySaveName.values())
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
            Network.WRAPPER.sendTo(new MultipleDialoguesPacket(found), player);
        }

        return true;
    }

    public static void add(CDialogue dialogue)
    {
        dialoguesBySaveName.put(dialogue.saveName.value, dialogue);
        dialoguesByID.put(dialogue.sessionID.value, dialogue);
    }

    public static CDialogue get(String saveName)
    {
        return dialoguesBySaveName.get(saveName);
    }

    public static CDialogue get(UUID id)
    {
        return dialoguesByID.get(id);
    }
}

package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.MultipleDialoguesPacket;
import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.rpgquesting.actions.CActionEndDialogue;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.quest.CRelatedDialogueEntry;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CDialogues extends Component
{
    public static final CDialogues DIALOGUES = new CDialogues();
    public static final LinkedHashMap<EntityPlayerMP, Pair<Entity, CDialogueBranch>> CURRENT_PLAYER_BRANCHES = new LinkedHashMap<>();

    //Only used on client
    public static int targetID = -1;

    public static LinkedHashMap<String, CDialogue> dialogues = new LinkedHashMap<>();
    public static LinkedHashMap<String, LinkedHashMap<String, CDialogue>> dialoguesByGroup = new LinkedHashMap<>();

    public static boolean entityInteract(EntityPlayerMP player, Entity entity)
    {
        CURRENT_PLAYER_BRANCHES.put(player, new Pair<>(entity, null));

        ArrayList<CDialogue> found = new ArrayList<>();
        for (CDialogue dialogue : dialogues.values())
        {
            if (dialogue.isAvailable(player, entity)) found.add(dialogue);
        }

        if (player.getDistanceSq(entity) > 25) return false;

        if (found.size() == 0) return false;
        else if (found.size() == 1) start(player, entity, found.get(0));
        else Network.WRAPPER.sendTo(new MultipleDialoguesPacket(found), player);

        return true;
    }

    public static void tryStart(EntityPlayerMP player, String dialogueName)
    {
        Pair<Entity, CDialogueBranch> currentData = CURRENT_PLAYER_BRANCHES.get(player);
        if (currentData == null) return;

        Entity target = currentData.getKey();
        if (target == null || target.getDistanceSq(player) > 25) return;


        CDialogues.start(player, target, get(dialogueName));
    }

    private static void start(EntityPlayerMP player, Entity target, CDialogue dialogue)
    {
        if (dialogue.branches.size() == 0) return;


        CDialogueBranch branch = dialogue.branches.get(0);
        CURRENT_PLAYER_BRANCHES.put(player, new Pair<>(target, branch));
        Network.branch(player, true, branch);
    }

    public static void tryMakeChoice(EntityPlayerMP player, String choice)
    {
        Pair<Entity, CDialogueBranch> currentData = CURRENT_PLAYER_BRANCHES.get(player);
        if (currentData == null) return;

        Entity target = currentData.getKey();
        if (target == null || target.getDistanceSq(player) > 25) return;

        CDialogueBranch currentBranch = currentData.getValue();
        if (currentBranch == null) return;

        CDialogueChoice found = null;
        for (CDialogueChoice choice2 : currentBranch.choices)
        {
            if (choice2.text.value.equals(choice))
            {
                found = choice2;
                break;
            }
        }
        if (found == null) return;


        if (found.action.getClass() != CActionEndDialogue.class && !get(currentBranch.dialogueName.value).isAvailable(player, target)) return;


        found.execute(player);
    }

    public static void add(CDialogue dialogue)
    {
        dialogues.put(dialogue.name.value, dialogue);
        dialoguesByGroup.computeIfAbsent(dialogue.group.value, o -> new LinkedHashMap<>()).put(dialogue.name.value, dialogue);

        int i = 0;
        for (CCondition condition : dialogue.playerConditions)
        {
            condition.updateRelations(dialogue.name.value, CRelatedDialogueEntry.TYPE_PLAYER_CONDITION, i++);
        }

        i = 0;
        for (CCondition condition : dialogue.entityConditions)
        {
            condition.updateRelations(dialogue.name.value, CRelatedDialogueEntry.TYPE_ENTITY_CONDITION, i++);
        }

        i = 0;
        for (CDialogueBranch branch : dialogue.branches)
        {
            for (CDialogueChoice choice : branch.choices)
            {
                for (CCondition condition : choice.availabilityConditions)
                {
                    condition.updateRelations(dialogue.name.value, CRelatedDialogueEntry.TYPE_BRANCH, i);
                }

                choice.action.updateRelations(dialogue.name.value, CRelatedDialogueEntry.TYPE_BRANCH, i);
            }
            i++;
        }
    }

    public static CDialogue get(String name)
    {
        return dialogues.get(name);
    }

    public static void delete(String dialogueName)
    {
        CDialogue dialogue = get(dialogueName);
        if (dialogue == null) return;


        for (CDialogueBranch branch : dialogue.branches)
        {
            CURRENT_PLAYER_BRANCHES.entrySet().removeIf(e ->
            {
                Pair<Entity, CDialogueBranch> currentData = e.getValue();
                if (currentData != null && currentData.getValue() == branch)
                {
                    Network.WRAPPER.sendTo(new Network.CloseDialoguePacket(), e.getKey());
                    return true;
                }
                return false;
            });
        }

        for (CQuest quest : CQuests.QUESTS.worldQuestData.values())
        {
            for (CRelatedDialogueEntry entry : quest.relatedDialogues.toArray(new CRelatedDialogueEntry[0]))
            {
                if (entry.dialogueName.value.equals(dialogueName)) quest.relatedDialogues.remove(entry);
            }
        }

        dialogues.remove(dialogueName);
        LinkedHashMap<String, CDialogue> group = dialoguesByGroup.get(dialogue.group.value);
        group.remove(dialogueName);
        if (group.size() == 0) dialoguesByGroup.remove(dialogue.group.value);


        try
        {
            DIALOGUES.save();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveDialogue(CDialogue dialogue)
    {
        //Cleanly remove old one if it exists, then add the new version
        delete(dialogue.name.value);
        add(dialogue);


        try
        {
            DIALOGUES.save();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public CDialogues save() throws IOException
    {
        File file = RPGQuesting.worldDataFolder;
        if (!file.exists()) file.mkdir();

        file = new File(file.getAbsolutePath() + File.separator + "Dialogues.dat");
        File file2 = new File(file.getAbsolutePath() + ".new");

        FileOutputStream fos = new FileOutputStream(file2);
        DIALOGUES.save(fos);
        fos.close();

        if (file.exists()) file.delete();
        file2.renameTo(file);

        return this;
    }

    public CDialogues clear()
    {
        dialogues.clear();
        dialoguesByGroup.clear();
        return this;
    }

    public CDialogues load() throws IOException
    {
        clear();

        File file = RPGQuesting.worldDataFolder;
        if (!file.exists()) return this;

        file = new File(file.getAbsolutePath() + File.separator + "Dialogues.dat");
        if (!file.exists()) return this;

        FileInputStream fis = new FileInputStream(file);
        DIALOGUES.load(fis);
        fis.close();

        return this;
    }

    @Override
    public CDialogues write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogues read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogues save(OutputStream stream)
    {
        new CInt().set(dialogues.size()).save(stream);
        for (CDialogue dialogue : dialogues.values()) dialogue.save(stream);
        return this;
    }

    @Override
    public CDialogues load(InputStream stream)
    {
        clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) add(new CDialogue().load(stream));
        return this;
    }
}

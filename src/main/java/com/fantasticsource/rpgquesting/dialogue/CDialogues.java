package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.MultipleDialoguesPacket;
import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CDialogues extends Component
{
    public static final CDialogues DIALOGUES = new CDialogues();
    public static final LinkedHashMap<EntityPlayerMP, Pair<Entity, CDialogueBranch>> CURRENT_PLAYER_BRANCHES = new LinkedHashMap<>();

    @SideOnly(Side.CLIENT)
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
        CDialogueBranch branch = dialogue.branches.get(0);
        CURRENT_PLAYER_BRANCHES.put(player, new Pair<>(target, branch));
        Network.WRAPPER.sendTo(new Network.DialogueBranchPacket(true, branch), player);
    }

    public static void tryMakeChoice(EntityPlayerMP player, String choice)
    {
        Pair<Entity, CDialogueBranch> currentData = CURRENT_PLAYER_BRANCHES.get(player);
        if (currentData == null) return;

        Entity target = currentData.getKey();
        if (target == null || target.getDistanceSq(player) > 25) return;

        CDialogueBranch currentBranch = currentData.getValue();
        if (currentBranch == null) return;

        if (!get(currentBranch.dialogueName.value).isAvailable(player, target)) return;

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


        found.execute(player);
    }

    public static void add(CDialogue dialogue)
    {
        dialogues.put(dialogue.name.value, dialogue);
        dialoguesByGroup.computeIfAbsent(dialogue.group.value, o -> new LinkedHashMap<>()).put(dialogue.name.value, dialogue);
    }

    public static CDialogue get(String name)
    {
        return dialogues.get(name);
    }

    public static void delete(String dialogueName)
    {

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

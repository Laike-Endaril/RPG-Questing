package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import com.fantasticsource.rpgquesting.Network.MultipleDialoguesPacket;
import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class CDialogues extends Component
{
    public static final CDialogues DIALOGUES = new CDialogues();
    public static int targetID = -1;
    public static LinkedHashMap<UUID, CDialogue> dialoguesByPermanentID = new LinkedHashMap<>();
    public static LinkedHashMap<UUID, CDialogue> dialoguesBySessionID = new LinkedHashMap<>();

    public static boolean entityInteract(EntityPlayerMP player, Entity entity)
    {
        ArrayList<CDialogue> found = new ArrayList<>();
        for (CDialogue dialogue : dialoguesByPermanentID.values())
        {
            if (dialogue.isAvailable(player, entity)) found.add(dialogue);
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
        dialoguesByPermanentID.put(dialogue.permanentID.value, dialogue);
        dialoguesBySessionID.put(dialogue.sessionID.value, dialogue);
    }

    public static CDialogue getByPermanentID(UUID id)
    {
        return dialoguesByPermanentID.get(id);
    }

    public static CDialogue getBySessionID(UUID id)
    {
        return dialoguesBySessionID.get(id);
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
        dialoguesByPermanentID.clear();
        dialoguesBySessionID.clear();
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
        new CInt().set(dialoguesByPermanentID.size()).save(stream);
        for (CDialogue dialogue : dialoguesByPermanentID.values()) dialogue.save(stream);
        return this;
    }

    @Override
    public CDialogues load(InputStream stream)
    {
        clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) new CDialogue().load(stream);
        return this;
    }
}

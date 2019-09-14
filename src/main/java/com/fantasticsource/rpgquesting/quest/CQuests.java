package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.UUID;

public class CQuests extends Component
{
    public static final CQuests QUESTS = new CQuests();
    private static LinkedHashMap<UUID, CPlayerQuestData> playerQuestData = new LinkedHashMap<>();
    private LinkedHashMap<UUID, CQuest> mainQuestData = new LinkedHashMap<>();

    public static void loadPlayerQuestData(EntityPlayerMP player) throws IOException
    {
        CPlayerQuestData data = new CPlayerQuestData(player).load();
        if (data != null) playerQuestData.put(player.getPersistentID(), data);
    }

    public static void unloadPlayerQuestData(EntityPlayerMP player) throws IOException
    {
        CPlayerQuestData data = playerQuestData.remove(player.getPersistentID());
        if (data != null) data.save();
    }

    public static boolean isInProgress(EntityPlayerMP player, CQuest quest)
    {
        return isCompleted(player.getPersistentID(), quest.permanentID.value);
    }

    public static boolean isInProgress(UUID playerID, CQuest quest)
    {
        return isCompleted(playerID, quest.permanentID.value);
    }

    public static boolean isInProgress(EntityPlayerMP player, UUID questID)
    {
        return isCompleted(player.getPersistentID(), questID);
    }

    public static boolean isInProgress(UUID playerID, UUID questID)
    {
        CPlayerQuestData data = playerQuestData.get(playerID);
        if (data == null) return false;

        return data.inProgressQuests.contains(questID);
    }

    public static boolean isCompleted(EntityPlayerMP player, CQuest quest)
    {
        return isCompleted(player.getPersistentID(), quest.permanentID.value);
    }

    public static boolean isCompleted(UUID playerID, CQuest quest)
    {
        return isCompleted(playerID, quest.permanentID.value);
    }

    public static boolean isCompleted(EntityPlayerMP player, UUID questID)
    {
        return isCompleted(player.getPersistentID(), questID);
    }

    public static boolean isCompleted(UUID playerID, UUID questID)
    {
        CPlayerQuestData data = playerQuestData.get(playerID);
        if (data == null) return false;

        return data.completedQuests.contains(questID);
    }

    public CQuests save() throws IOException
    {
        File file = RPGQuesting.dataFolder;
        if (!file.exists()) file.mkdir();

        file = new File(file.getAbsolutePath() + File.separator + "Quests.dat");
        File file2 = new File(file.getAbsolutePath() + ".new");

        FileOutputStream fos = new FileOutputStream(file2);
        QUESTS.save(fos);
        fos.close();

        if (file.exists()) file.delete();
        file2.renameTo(file);

        return this;
    }

    public CQuests load() throws IOException
    {
        mainQuestData.clear();
        playerQuestData.clear();

        File file = RPGQuesting.dataFolder;
        if (!file.exists()) return this;

        file = new File(file.getAbsolutePath() + File.separator + "Quests.dat");
        if (!file.exists()) return this;

        FileInputStream fis = new FileInputStream(file);
        QUESTS.load(fis);
        fis.close();

        return this;
    }

    @Override
    public CQuests write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CQuests read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CQuests save(OutputStream stream) throws IOException
    {
        new CInt().set(mainQuestData.size()).save(stream);
        for (CQuest quest : mainQuestData.values()) quest.save(stream);
        return this;
    }

    @Override
    public CQuests load(InputStream stream) throws IOException
    {
        mainQuestData.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--)
        {
            CQuest quest = new CQuest().load(stream);
            mainQuestData.put(quest.permanentID.value, quest);
        }
        return this;
    }
}

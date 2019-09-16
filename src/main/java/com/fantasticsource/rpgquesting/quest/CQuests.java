package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class CQuests extends Component
{
    public static final CQuests QUESTS = new CQuests();
    public static LinkedHashMap<UUID, CPlayerQuestData> playerQuestData = new LinkedHashMap<>();
    private LinkedHashMap<UUID, CQuest> mainQuestData = new LinkedHashMap<>();


    public static void add(CQuest quest)
    {
        QUESTS.mainQuestData.put(quest.permanentID.value, quest);
    }


    public static CQuest get(UUID id)
    {
        return QUESTS.mainQuestData.get(id);
    }


    public static void start(EntityPlayerMP player, UUID id)
    {
        CQuest quest = get(id);
        if (quest == null) return;

        CPlayerQuestData data = playerQuestData.computeIfAbsent(player.getPersistentID(), o -> new CPlayerQuestData(player));
        ArrayList<CObjective> objectives = data.inProgressQuests.computeIfAbsent(id, o -> new ArrayList<>());
        objectives.clear();
        for (CObjective objective : quest.objectives)
        {
            try
            {
                objectives.add((CObjective) objective.copy());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void abandon(EntityPlayerMP player, UUID id)
    {
        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null) return;

        data.inProgressQuests.remove(id);
    }

    public static boolean complete(EntityPlayerMP player, UUID id)
    {
        CPlayerQuestData data = playerQuestData.computeIfAbsent(player.getPersistentID(), o -> new CPlayerQuestData(player));
        if (!data.inProgressQuests.containsKey(id)) return false;

        data.inProgressQuests.remove(id);
        if (!data.completedQuests.contains(id)) data.completedQuests.add(id);

        CQuest quest = get(id);
        if (quest == null) return true;

        //TODO give exp/rewards
        return true;
    }


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


    public static boolean isAvailable(EntityPlayerMP player, CQuest quest)
    {
        return isAvailable(player, quest.permanentID.value);
    }

    public static boolean isAvailable(EntityPlayerMP player, UUID questID)
    {
        CQuest quest = QUESTS.mainQuestData.get(questID);
        if (quest == null) return false;

        return quest.isAvailable(player);
    }


    public static boolean isInProgress(EntityPlayerMP player, CQuest quest)
    {
        return isInProgress(player, quest.permanentID.value);
    }

    public static boolean isInProgress(EntityPlayerMP player, UUID questID)
    {
        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null) return false;

        ArrayList<CObjective> objectives = data.inProgressQuests.get(questID);
        if (objectives == null) return false;

        boolean done = true;
        for (CObjective objective : objectives) if (!objective.isDone()) done = false;
        return !done;
    }


    public static boolean isReadyToComplete(EntityPlayerMP player, CQuest quest)
    {
        return isReadyToComplete(player, quest.permanentID.value);
    }

    public static boolean isReadyToComplete(EntityPlayerMP player, UUID questID)
    {
        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null) return false;

        ArrayList<CObjective> objectives = data.inProgressQuests.get(questID);
        if (objectives == null) return false;

        for (CObjective objective : objectives) if (!objective.isDone()) return false;
        return true;
    }


    public static boolean isCompleted(EntityPlayerMP player, CQuest quest)
    {
        return isCompleted(player, quest.permanentID.value);
    }

    public static boolean isCompleted(EntityPlayerMP player, UUID questID)
    {
        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
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

    public CQuests clear()
    {
        playerQuestData.entrySet().removeIf(e ->
        {
            try
            {
                e.getValue().save();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            return true;
        });

        mainQuestData.clear();

        return this;
    }

    public CQuests load() throws IOException
    {
        playerQuestData.clear();
        mainQuestData.clear();

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

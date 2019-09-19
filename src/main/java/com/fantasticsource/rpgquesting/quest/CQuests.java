package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;

public class CQuests extends Component
{
    public static final CQuests QUESTS = new CQuests();
    public static LinkedHashMap<UUID, CPlayerQuestData> playerQuestData = new LinkedHashMap<>();
    public LinkedHashMap<String, CQuest> worldQuestData = new LinkedHashMap<>();
    public LinkedHashMap<String, LinkedHashMap<String, CQuest>> worldQuestDataByGroup = new LinkedHashMap<>();


    public static void add(CQuest quest)
    {
        QUESTS.worldQuestData.put(quest.name.value, quest);
        QUESTS.worldQuestDataByGroup.computeIfAbsent(quest.group.value, o -> new LinkedHashMap<>()).put(quest.name.value, quest);
    }


    public static boolean exists(CQuest quest)
    {
        Collection<CQuest> group = getGroup(quest.group.value);
        if (group == null) return false;

        return group.contains(quest);
    }

    @Nullable
    public static Collection<CQuest> getGroup(String group)
    {
        LinkedHashMap<String, CQuest> quests = QUESTS.worldQuestDataByGroup.get(group);
        if (quests == null) return null;

        return quests.values();
    }

    public static CQuest get(String name)
    {
        return QUESTS.worldQuestData.get(name);
    }


    public static void start(EntityPlayerMP player, String name)
    {
        CQuest quest = get(name);
        if (quest == null) return;

        CPlayerQuestData data = playerQuestData.computeIfAbsent(player.getPersistentID(), o -> new CPlayerQuestData(player));
        LinkedHashMap<String, ArrayList<CObjective>> map = data.inProgressQuests.computeIfAbsent(quest.group.value, o -> new LinkedHashMap<>());
        ArrayList<CObjective> objectives = map.computeIfAbsent(name, o -> new ArrayList<>());
        objectives.clear();
        for (CObjective objective : quest.objectives)
        {
            try
            {
                objectives.add(((CObjective) objective.copy()).setOwner(player));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        data.save();

        track(player, name);
    }

    public static void track(EntityPlayerMP player, String name)
    {
        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());


        if (name == null || name.equals(""))
        {
            Network.WRAPPER.sendTo(new Network.QuestTrackerPacket(name, new ArrayList<>()), player);
            if (data != null) data.trackedQuest.set("");
            return;
        }


        if (data == null) return;

        CQuest quest = get(name);
        if (quest == null) return;

        LinkedHashMap<String, ArrayList<CObjective>> q = data.inProgressQuests.get(quest.group.value);
        if (q == null) return;

        ArrayList<CObjective> objectives = q.get(name);
        if (objectives == null) return;


        data.trackedQuest.set(name);
        Network.WRAPPER.sendTo(new Network.QuestTrackerPacket(name, objectives), player);
    }

    public static void abandon(EntityPlayerMP player, String name)
    {
        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null) return;

        data.inProgressQuests.remove(name);

        data.save();

        if (data.trackedQuest.value.equals(name)) track(player, "");
    }

    public static boolean complete(EntityPlayerMP player, String name)
    {
        CQuest quest = get(name);
        if (quest == null) return false;

        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null) return false;

        String group = quest.group.value;
        LinkedHashMap<String, ArrayList<CObjective>> map = data.inProgressQuests.get(group);
        if (map == null || map.remove(name) == null) return false;

        if (map.size() == 0) data.inProgressQuests.remove(group);

        ArrayList<String> list = data.completedQuests.computeIfAbsent(group, o -> new ArrayList<>());
        if (!list.contains(name)) list.add(name);

        player.addExperience(quest.experience.value);
        for (CItemStack stack : quest.rewards)
        {
            player.addItemStackToInventory(stack.stack);
        }

        data.save();

        if (data.trackedQuest.value.equals(name)) track(player, "");

        return true;
    }


    public static void loadPlayerQuestData(EntityPlayerMP player) throws IOException
    {
        CPlayerQuestData data = new CPlayerQuestData(player).load();
        if (data.inProgressQuests.size() > 0 || data.completedQuests.size() > 0)
        {
            playerQuestData.put(player.getPersistentID(), data);
            track(player, data.trackedQuest.value);
        }
    }

    public static void unloadPlayerQuestData(EntityPlayerMP player)
    {
        CPlayerQuestData data = playerQuestData.remove(player.getPersistentID());
        if (data != null) data.save();
    }


    public static boolean isAvailable(EntityPlayerMP player, CQuest quest)
    {
        return isAvailable(player, quest.name.value);
    }

    public static boolean isAvailable(EntityPlayerMP player, String name)
    {
        CQuest quest = get(name);
        if (quest == null) return false;

        return quest.isAvailable(player);
    }


    public static boolean isInProgress(EntityPlayerMP player, CQuest quest)
    {
        return isInProgress(player, quest.name.value);
    }

    public static boolean isInProgress(EntityPlayerMP player, String name)
    {
        CQuest quest = get(name);
        if (quest == null) return false;

        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null) return false;

        LinkedHashMap<String, ArrayList<CObjective>> map = data.inProgressQuests.get(quest.group.value);
        if (map == null) return false;

        ArrayList<CObjective> objectives = map.get(quest.name.value);
        if (objectives == null) return false;

        boolean done = true;
        for (CObjective objective : objectives) if (!objective.isDone()) done = false;
        return !done;
    }


    public static boolean isReadyToComplete(EntityPlayerMP player, CQuest quest)
    {
        return isReadyToComplete(player, quest.name.value);
    }

    public static boolean isReadyToComplete(EntityPlayerMP player, String name)
    {
        CQuest quest = get(name);
        if (quest == null) return false;

        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null) return false;

        LinkedHashMap<String, ArrayList<CObjective>> map = data.inProgressQuests.get(quest.group.value);
        if (map == null) return false;

        ArrayList<CObjective> objectives = map.get(quest.name.value);
        if (objectives == null) return false;

        for (CObjective objective : objectives) if (!objective.isDone()) return false;
        return true;
    }


    public static boolean isCompleted(EntityPlayerMP player, CQuest quest)
    {
        return isCompleted(player, quest.name.value);
    }

    public static boolean isCompleted(EntityPlayerMP player, String name)
    {
        CQuest quest = get(name);
        if (quest == null) return false;

        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null) return false;

        ArrayList<String> list = data.completedQuests.get(quest.group.value);
        if (list == null) return false;

        return list.contains(name);
    }


    public CQuests save() throws IOException
    {
        File file = RPGQuesting.worldDataFolder;
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
            e.getValue().save();
            return true;
        });

        worldQuestData.clear();
        worldQuestDataByGroup.clear();

        return this;
    }

    public CQuests load() throws IOException
    {
        playerQuestData.clear();
        worldQuestData.clear();
        worldQuestDataByGroup.clear();

        File file = RPGQuesting.worldDataFolder;
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
        new CInt().set(worldQuestData.size()).save(stream);
        for (CQuest quest : worldQuestData.values()) quest.save(stream);
        return this;
    }

    @Override
    public CQuests load(InputStream stream) throws IOException
    {
        worldQuestData.clear();
        worldQuestDataByGroup.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--)
        {
            CQuest quest = new CQuest().load(stream);
            worldQuestData.put(quest.name.value, quest);
            QUESTS.worldQuestDataByGroup.computeIfAbsent(quest.group.value, o -> new LinkedHashMap<>()).put(quest.name.value, quest);
        }
        return this;
    }
}

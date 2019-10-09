package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
        LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> map = data.inProgressQuests.computeIfAbsent(quest.group.value, o -> new LinkedHashMap<>());
        Pair<CUUID, ArrayList<CObjective>> pair = map.computeIfAbsent(name, o -> new Pair<>(new CUUID().set(quest.permanentID.value), new ArrayList<>()));
        ArrayList<CObjective> objectives = pair.getValue();
        objectives.clear();
        for (CObjective objective : quest.objectives)
        {
            objectives.add(((CObjective) objective.copy()).setOwner(player));
        }

        data.saveAndSync();

        track(player, name);
    }

    public static void track(EntityPlayerMP player, String name)
    {
        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data != null)
        {
            data.trackedQuestName.set(name);
            data.saveAndSync();
        }
        else if (name == null || name.equals(""))
        {
            Network.WRAPPER.sendTo(new Network.QuestTrackerPacket(name, new ArrayList<>()), player);
            return;
        }
    }

    public static void abandon(EntityPlayerMP player, String questName)
    {
        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null) return;

        if (data.trackedQuestName.value.equals(questName)) track(player, "");

        CQuest quest = get(questName);
        if (quest == null) return;

        LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> group = data.inProgressQuests.get(quest.group.value);
        if (group == null) return;

        if (group.remove(questName) != null)
        {
            if (group.size() == 0) data.inProgressQuests.remove(quest.group.value);
            data.saveAndSync();
        }
    }

    public static void delete(String questname)
    {
        for (EntityPlayerMP playerMP : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
        {
            abandon(playerMP, questname);
        }

        CQuest quest = get(questname);
        QUESTS.worldQuestData.remove(questname);

        LinkedHashMap<String, CQuest> group = QUESTS.worldQuestDataByGroup.get(quest.group.value);
        if (group != null)
        {
            group.remove(questname);
            if (group.size() == 0) QUESTS.worldQuestDataByGroup.remove(quest.group.value);
        }
    }

    public static void saveQuest(CQuest quest)
    {
        //Cleanly remove old one if it exists, then add the new version
        delete(quest.name.value);
        add(quest);
    }

    public static void syncJournal(EntityPlayerMP player, String questToView, boolean openGUI)
    {
        //For quest to view, an empty or null string leaves it on whatever is already viewed (or sets it to the tracked quest if none is viewed and a quest is currently tracked)
        //To explicitly set it to the tracked quest, well...send the tracked quest name

        Network.WRAPPER.sendTo(new Network.JournalPacket(CQuests.playerQuestData.get(player.getPersistentID()), questToView, openGUI), player);
    }

    public static void syncEditor(EntityPlayerMP player, boolean openGUI)
    {
        if (player.interactionManager.getGameType() != GameType.CREATIVE) return;

        Network.WRAPPER.sendTo(new Network.EditorPacket(openGUI), player);
    }

    public static void syncTracker(EntityPlayerMP player)
    {
        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null)
        {
            Network.WRAPPER.sendTo(new Network.QuestTrackerPacket("", new ArrayList<>()), player);
            return;
        }


        String name = data.trackedQuestName.value;
        CQuest quest = get(name);
        if (quest == null)
        {
            Network.WRAPPER.sendTo(new Network.QuestTrackerPacket("", new ArrayList<>()), player);
            return;
        }

        LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> group = data.inProgressQuests.get(quest.group.value);
        if (group == null)
        {
            Network.WRAPPER.sendTo(new Network.QuestTrackerPacket("", new ArrayList<>()), player);
            return;
        }

        Pair<CUUID, ArrayList<CObjective>> pair = group.get(name);
        if (pair == null)
        {
            Network.WRAPPER.sendTo(new Network.QuestTrackerPacket("", new ArrayList<>()), player);
            return;
        }

        Network.WRAPPER.sendTo(new Network.QuestTrackerPacket(name, pair.getValue()), player);
    }

    public static boolean complete(EntityPlayerMP player, String name)
    {
        CQuest quest = get(name);
        if (quest == null) return false;

        CPlayerQuestData data = playerQuestData.get(player.getPersistentID());
        if (data == null) return false;

        String group = quest.group.value;
        LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> map = data.inProgressQuests.get(group);
        if (map == null || map.remove(name) == null) return false;

        if (map.size() == 0) data.inProgressQuests.remove(group);

        ArrayList<String> list = data.completedQuests.computeIfAbsent(group, o -> new ArrayList<>());
        if (!list.contains(name)) list.add(name);

        player.addExperience(quest.experience.value);
        for (CItemStack stack : quest.rewards)
        {
            player.addItemStackToInventory(stack.stack.copy());
        }


        if (data.trackedQuestName.value.equals(name)) data.trackedQuestName.set("");
        data.saveAndSync();

        return true;
    }


    public static void loadPlayerQuestData(EntityPlayerMP player) throws IOException
    {
        CPlayerQuestData data = new CPlayerQuestData(player).load();
        playerQuestData.put(player.getPersistentID(), data);
        track(player, data.trackedQuestName.value);
    }

    public static void unloadPlayerQuestData(EntityPlayerMP player)
    {
        CPlayerQuestData data = playerQuestData.remove(player.getPersistentID());
        if (data != null) data.saveAndSync();
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

        LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> map = data.inProgressQuests.get(quest.group.value);
        if (map == null) return false;

        Pair<CUUID, ArrayList<CObjective>> pair = map.get(quest.name.value);
        if (pair == null) return false;

        boolean done = true;
        for (CObjective objective : pair.getValue()) if (!objective.isDone()) done = false;
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

        LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> map = data.inProgressQuests.get(quest.group.value);
        if (map == null) return false;

        Pair<CUUID, ArrayList<CObjective>> pair = map.get(quest.name.value);
        if (pair == null) return false;

        for (CObjective objective : pair.getValue()) if (!objective.isDone()) return false;
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
            e.getValue().saveAndSync();
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
    public CQuests save(OutputStream stream)
    {
        new CInt().set(worldQuestData.size()).save(stream);
        for (CQuest quest : worldQuestData.values()) quest.save(stream);
        return this;
    }

    @Override
    public CQuests load(InputStream stream)
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

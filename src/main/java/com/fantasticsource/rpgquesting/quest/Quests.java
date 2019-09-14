package com.fantasticsource.rpgquesting.quest;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.UUID;

public class Quests
{
    private static LinkedHashMap<UUID, CQuestPlayerData> playerQuestData = new LinkedHashMap<>();


    public static void loadMainQuestData(FMLServerAboutToStartEvent event)
    {
        playerQuestData.clear();
        //TODO
    }

    public static void unloadMainQuestData(FMLServerStoppedEvent event)
    {
        playerQuestData.clear();
    }

    public static void loadPlayerQuestData(EntityPlayerMP player) throws IOException
    {
        CQuestPlayerData data = new CQuestPlayerData(player).load();
        if (data != null) playerQuestData.put(player.getPersistentID(), data);
    }

    public static void unloadPlayerQuestData(EntityPlayerMP player) throws IOException
    {
        CQuestPlayerData data = playerQuestData.remove(player.getPersistentID());
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
        CQuestPlayerData data = playerQuestData.get(playerID);
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
        CQuestPlayerData data = playerQuestData.get(playerID);
        if (data == null) return false;

        return data.completedQuests.contains(questID);
    }
}

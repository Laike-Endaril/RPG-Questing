package com.fantasticsource.rpgquesting.quest;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.LinkedHashMap;
import java.util.UUID;

public class Quests
{
    private static LinkedHashMap<UUID, CQuestData> playerQuestData = new LinkedHashMap<>();


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
        CQuestData data = playerQuestData.get(playerID);
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
        CQuestData data = playerQuestData.get(playerID);
        if (data == null) return false;

        return data.completedQuests.contains(questID);
    }
}

package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;

public class CConditionQuestAvailable extends CQuestCondition
{
    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            CQuest quest = CQuests.get(name.value);
            if (quest == null) result.add("Quest must be available (quest does not exist!): \"" + name.value + '"');
            else if (!quest.isAvailable((EntityPlayerMP) entity)) result.add("Quest must be available: \"" + quest.name.value + '"');
        }
        return result;
    }
}

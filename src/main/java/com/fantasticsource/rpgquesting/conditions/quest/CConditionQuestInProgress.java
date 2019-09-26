package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;

public class CConditionQuestInProgress extends CQuestCondition
{
    public CConditionQuestInProgress()
    {
        super();
    }

    public CConditionQuestInProgress(CQuest quest, CDialogueBranch branch)
    {
        super(quest, branch);
    }

    @Override
    public String relation()
    {
        return "Requires quest be in progress (not ready to be completed)";
    }


    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            CQuest quest = CQuests.get(name.value);
            if (quest == null) result.add("Quest must be in progress (quest does not exist!): \"" + name.value + '"');
            else if (!quest.isInProgress((EntityPlayerMP) entity)) result.add("Quest must be in progress: \"" + quest.name.value + '"');
        }
        return result;
    }
}

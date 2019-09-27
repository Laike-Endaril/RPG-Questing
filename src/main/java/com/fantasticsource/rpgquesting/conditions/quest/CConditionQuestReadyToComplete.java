package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;

public class CConditionQuestReadyToComplete extends CQuestCondition
{
    public CConditionQuestReadyToComplete()
    {
        super();
    }

    public CConditionQuestReadyToComplete(CQuest quest, CDialogueBranch branch)
    {
        super(quest, branch);
    }

    @Override
    public String relation()
    {
        return "requires that this quest be ready to turn in";
    }


    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            CQuest quest = CQuests.get(name.value);
            if (quest == null) result.add("Quest must be ready to be completed (quest does not exist!): \"" + name + '"');
            else if (!quest.isReadyToComplete((EntityPlayerMP) entity)) result.add("Quest must be ready to be completed: \"" + quest.name.value + '"');
        }
        return result;
    }

    @Override
    public String description()
    {
        return "Requires quest be ready to turn in: " + name.value;
    }
}

package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionQuestReadyToComplete extends CCondition
{
    public CStringUTF8 questName = new CStringUTF8();


    public CConditionQuestReadyToComplete()
    {
    }

    public CConditionQuestReadyToComplete(CQuest quest)
    {
        this(quest.name.value);
    }

    public CConditionQuestReadyToComplete(String name)
    {
        set(name);
    }


    public CConditionQuestReadyToComplete set(CQuest quest)
    {
        return set(quest.name.value);
    }

    public CConditionQuestReadyToComplete set(String name)
    {
        questName.set(name);
        return this;
    }


    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            CQuest quest = CQuests.get(questName.value);
            if (quest == null) result.add("Quest must be ready to be completed (quest does not exist!): \"" + questName + '"');
            else if (!quest.isReadyToComplete((EntityPlayerMP) entity)) result.add("Quest must be ready to be completed: \"" + quest.name.value + '"');
        }
        return result;
    }

    @Override
    public CConditionQuestReadyToComplete write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionQuestReadyToComplete read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionQuestReadyToComplete save(OutputStream stream)
    {
        questName.save(stream);
        return this;
    }

    @Override
    public CConditionQuestReadyToComplete load(InputStream stream)
    {
        questName.load(stream);
        return this;
    }
}

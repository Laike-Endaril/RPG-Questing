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

public class CConditionQuestCompleted extends CCondition
{
    public CStringUTF8 questName = new CStringUTF8();


    public CConditionQuestCompleted()
    {
    }

    public CConditionQuestCompleted(CQuest quest)
    {
        this(quest.name.value);
    }

    public CConditionQuestCompleted(String name)
    {
        set(name);
    }


    public CConditionQuestCompleted set(CQuest quest)
    {
        return set(quest.name.value);
    }

    public CConditionQuestCompleted set(String name)
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
            if (quest == null) result.add("Quest must be completed (quest does not exist!): \"" + questName.value + '"');
            else if (!quest.isCompleted((EntityPlayerMP) entity)) result.add("Quest must be completed: \"" + quest.name.value + '"');
        }
        return result;
    }

    @Override
    public CConditionQuestCompleted write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionQuestCompleted read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionQuestCompleted save(OutputStream stream)
    {
        questName.save(stream);
        return this;
    }

    @Override
    public CConditionQuestCompleted load(InputStream stream)
    {
        questName.load(stream);
        return this;
    }
}

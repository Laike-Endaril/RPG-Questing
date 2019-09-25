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

public class CConditionQuestInProgress extends CCondition
{
    public CStringUTF8 questName = new CStringUTF8();


    public CConditionQuestInProgress()
    {
    }

    public CConditionQuestInProgress(CQuest quest)
    {
        this(quest.name.value);
    }

    public CConditionQuestInProgress(String name)
    {
        set(name);
    }


    public CConditionQuestInProgress set(CQuest quest)
    {
        return set(quest.name.value);
    }

    public CConditionQuestInProgress set(String name)
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
            if (quest == null) result.add("Quest must be in progress (quest does not exist!): \"" + questName.value + '"');
            else if (!quest.isInProgress((EntityPlayerMP) entity)) result.add("Quest must be in progress: \"" + quest.name.value + '"');
        }
        return result;
    }

    @Override
    public CConditionQuestInProgress write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionQuestInProgress read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionQuestInProgress save(OutputStream stream)
    {
        questName.save(stream);
        return this;
    }

    @Override
    public CConditionQuestInProgress load(InputStream stream)
    {
        questName.load(stream);
        return this;
    }
}

package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionQuestAvailable extends CCondition
{
    public CStringUTF8 questName = new CStringUTF8();


    public CConditionQuestAvailable()
    {
    }

    public CConditionQuestAvailable(CQuest quest)
    {
        this(quest.name.value);
    }

    public CConditionQuestAvailable(String name)
    {
        set(name);
    }


    public CConditionQuestAvailable set(CQuest quest)
    {
        return set(quest.name.value);
    }

    public CConditionQuestAvailable set(String name)
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
            if (quest == null) result.add("Quest must be available (quest does not exist!): \"" + questName.value + '"');
            else if (!quest.isAvailable((EntityPlayerMP) entity)) result.add("Quest must be available: \"" + quest.name.value + '"');
        }
        return result;
    }

    @Override
    public CConditionQuestAvailable write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionQuestAvailable read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionQuestAvailable save(OutputStream stream) throws IOException
    {
        questName.save(stream);
        return this;
    }

    @Override
    public CConditionQuestAvailable load(InputStream stream) throws IOException
    {
        questName.load(stream);
        return this;
    }
}

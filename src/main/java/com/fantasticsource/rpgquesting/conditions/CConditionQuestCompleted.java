package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CUUID;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class CConditionQuestCompleted extends CCondition
{
    public CUUID permanentQuestID = new CUUID();


    public CConditionQuestCompleted()
    {
    }

    public CConditionQuestCompleted(CQuest quest)
    {
        this(quest.permanentID.value);
    }

    public CConditionQuestCompleted(UUID questID)
    {
        set(questID);
    }


    public CConditionQuestCompleted set(CQuest quest)
    {
        return set(quest.permanentID.value);
    }

    public CConditionQuestCompleted set(UUID questID)
    {
        permanentQuestID.set(questID);
        return this;
    }


    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            CQuest quest = CQuests.get(permanentQuestID.value);
            if (quest == null) result.add("Quest must be completed (quest does not exist!): \"" + permanentQuestID.value + '"');
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
    public CConditionQuestCompleted save(OutputStream stream) throws IOException
    {
        permanentQuestID.save(stream);
        return this;
    }

    @Override
    public CConditionQuestCompleted load(InputStream stream) throws IOException
    {
        permanentQuestID.load(stream);
        return this;
    }
}

package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CUUID;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionQuestInProgress extends CCondition
{
    public CUUID permanentQuestID = new CUUID();

    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            if (!CQuests.isInProgress((EntityPlayerMP) entity, permanentQuestID.value)) result.add("Quest must be in progress: \"" + CQuests.get(permanentQuestID.value).name.value + '"');
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
    public CConditionQuestInProgress save(OutputStream stream) throws IOException
    {
        permanentQuestID.save(stream);
        return this;
    }

    @Override
    public CConditionQuestInProgress load(InputStream stream) throws IOException
    {
        permanentQuestID.load(stream);
        return this;
    }
}

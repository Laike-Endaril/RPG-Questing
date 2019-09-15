package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CUUID;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CConditionQuestInProgress extends CCondition
{
    public CUUID permanentQuestID = new CUUID();

    @Override
    public boolean check(EntityPlayerMP player)
    {
        return CQuests.isInProgress(player, permanentQuestID.value);
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

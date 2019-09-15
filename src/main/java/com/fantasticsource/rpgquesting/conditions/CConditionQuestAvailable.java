package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CUUID;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CConditionQuestAvailable extends CCondition
{
    public CUUID permanentQuestID = new CUUID();

    @Override
    public boolean check(EntityPlayerMP player)
    {
        return CQuests.isAvailable(player, permanentQuestID.value);
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
        permanentQuestID.save(stream);
        return this;
    }

    @Override
    public CConditionQuestAvailable load(InputStream stream) throws IOException
    {
        permanentQuestID.load(stream);
        return this;
    }
}

package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CUUID;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class CActionCompleteQuest extends CAction
{
    CUUID questID = new CUUID();

    public CActionCompleteQuest()
    {
    }

    public CActionCompleteQuest(CQuest quest)
    {
        this(quest.permanentID.value);
    }

    public CActionCompleteQuest(UUID questID)
    {
        set(questID);
    }


    public CActionCompleteQuest set(CQuest quest)
    {
        return set(quest.permanentID.value);
    }

    public CActionCompleteQuest set(UUID id)
    {
        this.questID.set(id);

        return this;
    }


    @Override
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        CQuests.complete((EntityPlayerMP) entity, questID.value);
    }

    @Override
    public CActionCompleteQuest write(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionCompleteQuest read(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionCompleteQuest save(OutputStream stream) throws IOException
    {
        questID.save(stream);

        return this;
    }

    @Override
    public CActionCompleteQuest load(InputStream stream) throws IOException
    {
        questID.load(stream);

        return this;
    }
}

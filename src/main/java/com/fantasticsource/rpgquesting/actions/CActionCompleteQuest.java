package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
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

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CActionCompleteQuest load(InputStream stream) throws IOException
    {
        questID.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }
}

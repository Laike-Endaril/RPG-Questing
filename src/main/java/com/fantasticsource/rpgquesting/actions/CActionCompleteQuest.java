package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CActionCompleteQuest extends CAction
{
    CStringUTF8 questName = new CStringUTF8();

    public CActionCompleteQuest()
    {
    }

    public CActionCompleteQuest(CQuest quest)
    {
        this(quest.name.value);
    }

    public CActionCompleteQuest(String name)
    {
        set(name);
    }


    public CActionCompleteQuest set(CQuest quest)
    {
        return set(quest.name.value);
    }

    public CActionCompleteQuest set(String name)
    {
        this.questName.set(name);

        return this;
    }


    @Override
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        CQuests.complete((EntityPlayerMP) entity, questName.value);
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
        questName.save(stream);

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CActionCompleteQuest load(InputStream stream) throws IOException
    {
        questName.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }
}

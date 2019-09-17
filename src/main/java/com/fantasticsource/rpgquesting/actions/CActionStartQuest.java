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

public class CActionStartQuest extends CAction
{
    CStringUTF8 name = new CStringUTF8();

    public CActionStartQuest()
    {
    }

    public CActionStartQuest(CQuest quest)
    {
        this(quest.name.value);
    }

    public CActionStartQuest(String name)
    {
        set(name);
    }


    public CActionStartQuest set(CQuest quest)
    {
        return set(quest.name.value);
    }

    public CActionStartQuest set(String name)
    {
        this.name.set(name);

        return this;
    }


    @Override
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        CQuests.start((EntityPlayerMP) entity, name.value);
    }

    @Override
    public CActionStartQuest write(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionStartQuest read(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionStartQuest save(OutputStream stream) throws IOException
    {
        name.save(stream);

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CActionStartQuest load(InputStream stream) throws IOException
    {
        name.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }
}

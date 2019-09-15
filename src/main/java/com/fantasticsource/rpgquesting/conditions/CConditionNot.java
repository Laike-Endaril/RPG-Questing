package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CConditionNot extends CCondition
{
    public CCondition condition = null;

    @Override
    public boolean check(Entity entity)
    {
        return !condition.check(entity);
    }

    @Override
    public CConditionNot write(ByteBuf buf)
    {
        Component.writeMarked(buf, condition);
        return this;
    }

    @Override
    public CConditionNot read(ByteBuf buf)
    {
        condition = ((CCondition) Component.readMarked(buf));
        return this;
    }

    @Override
    public CConditionNot save(OutputStream stream) throws IOException
    {
        Component.saveMarked(stream, condition);
        return this;
    }

    @Override
    public CConditionNot load(InputStream stream) throws IOException
    {
        condition = ((CCondition) Component.loadMarked(stream));
        return this;
    }
}

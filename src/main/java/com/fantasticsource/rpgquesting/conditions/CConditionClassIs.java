package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CConditionClassIs extends CCondition
{
    public CStringUTF8 className = new CStringUTF8();

    @Override
    public boolean check(Entity entity)
    {
        return entity.getClass().getName().equals(className.value);
    }

    @Override
    public CConditionClassIs write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionClassIs read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionClassIs save(OutputStream stream) throws IOException
    {
        className.save(stream);
        return this;
    }

    @Override
    public CConditionClassIs load(InputStream stream) throws IOException
    {
        className.load(stream);
        return this;
    }
}

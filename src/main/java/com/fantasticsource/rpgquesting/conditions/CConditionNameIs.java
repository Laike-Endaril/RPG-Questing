package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CConditionNameIs extends CCondition
{
    public CStringUTF8 name = new CStringUTF8();

    @Override
    public boolean check(Entity entity)
    {
        return entity.getName().equals(name.value);
    }

    @Override
    public CConditionNameIs write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionNameIs read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionNameIs save(OutputStream stream) throws IOException
    {
        name.save(stream);
        return this;
    }

    @Override
    public CConditionNameIs load(InputStream stream) throws IOException
    {
        name.load(stream);
        return this;
    }
}

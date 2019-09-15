package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionAnd extends CCondition
{
    public ArrayList<CCondition> anded = new ArrayList<>();


    @Override
    public boolean check(Entity entity)
    {
        for (CCondition condition : anded) if (!condition.check(entity)) return false;
        return true;
    }

    @Override
    public CConditionAnd write(ByteBuf buf)
    {
        buf.writeInt(anded.size());
        for (CCondition condition : anded) Component.writeMarked(buf, condition);
        return this;
    }

    @Override
    public CConditionAnd read(ByteBuf buf)
    {
        for (int i = buf.readInt(); i > 0; i--) anded.add((CCondition) Component.readMarked(buf));
        return this;
    }

    @Override
    public CConditionAnd save(OutputStream stream) throws IOException
    {
        new CInt().set(anded.size()).save(stream);
        for (CCondition condition : anded) Component.saveMarked(stream, condition);
        return this;
    }

    @Override
    public CConditionAnd load(InputStream stream) throws IOException
    {
        for (int i = new CInt().load(stream).value; i > 0; i--) anded.add((CCondition) Component.loadMarked(stream));
        return this;
    }
}

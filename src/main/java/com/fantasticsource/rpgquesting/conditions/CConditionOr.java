package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionOr extends CCondition
{
    public ArrayList<CCondition> orred = new ArrayList<>();


    @Override
    public boolean check(Entity entity)
    {
        for (CCondition condition : orred) if (condition.check(entity)) return true;
        return false;
    }

    @Override
    public CConditionOr write(ByteBuf buf)
    {
        buf.writeInt(orred.size());
        for (CCondition condition : orred) Component.writeMarked(buf, condition);
        return this;
    }

    @Override
    public CConditionOr read(ByteBuf buf)
    {
        for (int i = buf.readInt(); i > 0; i--) orred.add((CCondition) Component.readMarked(buf));
        return this;
    }

    @Override
    public CConditionOr save(OutputStream stream) throws IOException
    {
        new CInt().set(orred.size()).save(stream);
        for (CCondition condition : orred) Component.saveMarked(stream, condition);
        return this;
    }

    @Override
    public CConditionOr load(InputStream stream) throws IOException
    {
        for (int i = new CInt().load(stream).value; i > 0; i--) orred.add((CCondition) Component.loadMarked(stream));
        return this;
    }
}

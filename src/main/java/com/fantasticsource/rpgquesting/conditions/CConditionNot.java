package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionNot extends CCondition
{
    public CCondition condition = null;

    public CConditionNot()
    {
    }

    public CConditionNot(CCondition notThis)
    {
        condition = notThis;
    }

    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = condition.unmetConditions(entity);

        if (result.size() > 0) return new ArrayList<>();


        result.add(0, "{");
        result.add("}");
        result.add(0, "NOT:");

        return result;
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

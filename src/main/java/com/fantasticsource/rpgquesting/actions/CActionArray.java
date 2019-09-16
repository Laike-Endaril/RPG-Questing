package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import scala.actors.threadpool.Arrays;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CActionArray extends CAction
{
    public ArrayList<CAction> actions = new ArrayList<>();

    public CActionArray()
    {
    }

    public CActionArray(CAction... actions)
    {
        add(actions);
    }

    public CActionArray add(CAction... actions)
    {
        this.actions.addAll(Arrays.asList(actions));
        return this;
    }

    @Override
    protected void execute(Entity entity)
    {
        for (CAction action : actions) action.execute(entity);
    }

    @Override
    public CActionArray write(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionArray read(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionArray save(OutputStream stream) throws IOException
    {
        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        new CInt().set(actions.size()).save(stream);
        for (CAction action : actions) Component.saveMarked(stream, action);

        return this;
    }

    @Override
    public CActionArray load(InputStream stream) throws IOException
    {
        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        actions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) actions.add((CAction) Component.loadMarked(stream));

        return this;
    }
}

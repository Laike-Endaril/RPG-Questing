package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import scala.actors.threadpool.Arrays;

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
    public String description()
    {
        if (actions.size() == 0) return "Does nothing";

        if (actions.size() == 1) return actions.get(0).description();

        StringBuilder s = new StringBuilder("Does all of these things:\n{");
        for (CAction action : actions) s.append("\n").append(action.description());
        s.append("\n}");
        return s.toString();
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
    public CActionArray save(OutputStream stream)
    {
        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        new CInt().set(actions.size()).save(stream);
        for (CAction action : actions) Component.saveMarked(stream, action);

        return this;
    }

    @Override
    public CActionArray load(InputStream stream)
    {
        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        actions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) actions.add((CAction) Component.loadMarked(stream));

        return this;
    }
}

package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.gui.GUIAction;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

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
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();

        for (CAction action : actions)
        {
            result.addAll(action.description());
        }

        for (int i = 0; i < result.size(); i++)
        {
            result.set(i, " " + result.get(i));
        }

        result.add(0, "{");
        result.add("}");
        result.add(0, "ALL OF:");

        return result;
    }

    @Override
    public CActionArray write(ByteBuf buf)
    {
        new CInt().set(conditions.size()).write(buf);
        for (CCondition condition : conditions) Component.writeMarked(buf, condition);

        new CInt().set(actions.size()).write(buf);
        for (CAction action : actions) Component.writeMarked(buf, action);

        return this;
    }

    @Override
    public CActionArray read(ByteBuf buf)
    {
        conditions.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--) conditions.add((CCondition) Component.readMarked(buf));

        actions.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--) actions.add((CAction) Component.readMarked(buf));

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

    @Override
    public GUIAction getChoosableElement(GUIScreen screen)
    {
        GUIAction element = new GUIAction(screen, new CActionArray());
        element.text = "Array (multiple actions)";
        return element;
    }
}

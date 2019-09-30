package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.conditions.gui.GUICondition;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import scala.actors.threadpool.Arrays;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionAnd extends CCondition
{
    public ArrayList<CCondition> conditions = new ArrayList<>();


    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        for (CCondition condition : conditions)
        {
            result.addAll(condition.unmetConditions(entity));
        }

        if (result.size() > 0)
        {
            for (int i = 0; i < result.size(); i++)
            {
                result.set(i, " " + result.get(i));
            }

            result.add(0, "{");
            result.add("}");
            result.add(0, "ALL OF:");
        }

        return result;
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();

        for (CCondition condition : conditions)
        {
            result.addAll(condition.description());
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
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        GUICondition conditionElement = new GUICondition(screen, new CConditionAnd());
        conditionElement.text = conditionElement.text.replace("nothing", "all of multiple conditions (AND)...");
        return conditionElement;
    }

    public CConditionAnd add(CCondition... conditions)
    {
        this.conditions.addAll(Arrays.asList(conditions));
        return this;
    }

    @Override
    public CConditionAnd write(ByteBuf buf)
    {
        buf.writeInt(conditions.size());
        for (CCondition condition : conditions) Component.writeMarked(buf, condition);
        return this;
    }

    @Override
    public CConditionAnd read(ByteBuf buf)
    {
        for (int i = buf.readInt(); i > 0; i--) conditions.add((CCondition) Component.readMarked(buf));
        return this;
    }

    @Override
    public CConditionAnd save(OutputStream stream)
    {
        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);
        return this;
    }

    @Override
    public CConditionAnd load(InputStream stream)
    {
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));
        return this;
    }
}

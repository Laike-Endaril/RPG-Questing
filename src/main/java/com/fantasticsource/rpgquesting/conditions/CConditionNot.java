package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.conditions.gui.GUICondition;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

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
        if (condition == null) return new ArrayList<>();

        ArrayList<String> result = condition.unmetConditions(entity);

        if (result.size() > 0) return new ArrayList<>();


        result.add(0, "{");
        result.add("}");
        result.add(0, "NOT:");

        return result;
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = condition == null ? new ArrayList<>() : condition.description();

        result.add(0, "{");
        result.add("}");
        result.add(0, "NOT:");

        return result;
    }

    @Override
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        GUICondition element = new GUICondition(screen, new CConditionNot());
        element.text = "NOT (opposite of a condition)";
        return element;
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
    public CConditionNot save(OutputStream stream)
    {
        Component.saveMarked(stream, condition);
        return this;
    }

    @Override
    public CConditionNot load(InputStream stream)
    {
        condition = ((CCondition) Component.loadMarked(stream));
        return this;
    }
}

package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.selectionguis.GUICondition;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionClassIs extends CCondition
{
    public CStringUTF8 className = new CStringUTF8();

    public CConditionClassIs()
    {
    }

    public CConditionClassIs(String className)
    {
        this.className.set(className);
    }

    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();
        if (!entity.getClass().getName().equals(className.value)) result.add("Entity class must be " + className.value);
        return result;
    }

    @Override
    public String description()
    {
        return "Requires entity class: " + TextFormatting.GOLD + className.value;
    }

    @Override
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        return new GUICondition(screen, new CConditionClassIs("packages.Classname"));
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
    public CConditionClassIs save(OutputStream stream)
    {
        className.save(stream);
        return this;
    }

    @Override
    public CConditionClassIs load(InputStream stream)
    {
        className.load(stream);
        return this;
    }
}

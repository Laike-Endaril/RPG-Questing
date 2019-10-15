package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUICondition;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionNameIs extends CCondition
{
    public CStringUTF8 name = new CStringUTF8();

    public CConditionNameIs()
    {
    }

    public CConditionNameIs(String name)
    {
        set(name);
    }

    public CConditionNameIs set(String name)
    {
        this.name.set(name);
        return this;
    }

    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();
        if (!entity.getName().equals(name.value)) result.add("Entity name must be \"" + name.value + '"');
        return result;
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Entity name: " + name.value);
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        return new GUICondition(screen, new CConditionNameIs("Name"));
    }

    @Override
    public CConditionNameIs write(ByteBuf buf)
    {
        name.write(buf);

        return this;
    }

    @Override
    public CConditionNameIs read(ByteBuf buf)
    {
        name.read(buf);

        return this;
    }

    @Override
    public CConditionNameIs save(OutputStream stream)
    {
        name.save(stream);
        return this;
    }

    @Override
    public CConditionNameIs load(InputStream stream)
    {
        name.load(stream);
        return this;
    }
}

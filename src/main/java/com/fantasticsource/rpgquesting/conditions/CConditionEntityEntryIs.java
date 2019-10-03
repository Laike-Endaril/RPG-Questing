package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUICondition;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionEntityEntryIs extends CCondition
{
    public CStringUTF8 entityEntryName = new CStringUTF8();

    public CConditionEntityEntryIs()
    {
    }

    public CConditionEntityEntryIs(String entityEntryName)
    {
        set(entityEntryName);
    }

    public CConditionEntityEntryIs set(String entityEntryName)
    {
        this.entityEntryName.set(entityEntryName);
        return this;
    }

    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityEntryName.value));
        if (!entity.getClass().equals(entry.getEntityClass())) result.add("Entity entry must be " + entityEntryName.value);
        return result;
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Entity entry: " + entityEntryName.value);
        return result;
    }

    @Override
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        return new GUICondition(screen, new CConditionEntityEntryIs("domain:name"));
    }

    @Override
    public CConditionEntityEntryIs write(ByteBuf buf)
    {
        entityEntryName.write(buf);

        return this;
    }

    @Override
    public CConditionEntityEntryIs read(ByteBuf buf)
    {
        entityEntryName.read(buf);

        return this;
    }

    @Override
    public CConditionEntityEntryIs save(OutputStream stream)
    {
        entityEntryName.save(stream);
        return this;
    }

    @Override
    public CConditionEntityEntryIs load(InputStream stream)
    {
        entityEntryName.load(stream);
        return this;
    }
}

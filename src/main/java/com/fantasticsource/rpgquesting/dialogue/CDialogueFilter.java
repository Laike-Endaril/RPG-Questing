package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CInt;
import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.CUUID;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CDialogueFilter extends Component
{
    private ArrayList<CUUID> allowedEntities = new ArrayList<>();
    private ArrayList<EntityEntry> allowedEntityEntries = new ArrayList<>();
    private ArrayList<CStringUTF8> allowedEntityNames = new ArrayList<>();

    public CDialogueFilter(Entity... entities)
    {
        for (Entity entity : entities)
        {
            allowedEntities.add(new CUUID().set(entity.getPersistentID()));
        }
    }

    public CDialogueFilter add(Entity entity)
    {
        allowedEntities.add(new CUUID().set(entity.getPersistentID()));
        return this;
    }

    public CDialogueFilter add(ResourceLocation rl)
    {
        EntityEntry entry = ForgeRegistries.ENTITIES.getValue(rl);
        if (entry != null) add(entry);
        return this;
    }

    public CDialogueFilter add(EntityEntry entry)
    {
        allowedEntityEntries.add(entry);
        return this;
    }

    public CDialogueFilter add(String entityName)
    {
        allowedEntityNames.add(new CStringUTF8().set(entityName));
        return this;
    }

    public boolean allowed(Entity entity)
    {
        for (CUUID id : allowedEntities)
        {
            if (entity.getPersistentID().equals(id.value)) return true;
        }

        boolean found = allowedEntityEntries.size() == 0;
        for (EntityEntry entry : allowedEntityEntries)
        {
            if (entry.getEntityClass() == entity.getClass())
            {
                found = true;
                break;
            }
        }
        if (!found) return false;

        found = allowedEntityNames.size() == 0;
        for (CStringUTF8 name : allowedEntityNames)
        {
            if (name.value.equals(entity.getName()))
            {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public CDialogueFilter write(ByteBuf buf)
    {
        return null;
    }

    @Override
    public CDialogueFilter read(ByteBuf buf)
    {
        return null;
    }

    @Override
    public CDialogueFilter save(FileOutputStream stream) throws IOException
    {
        CInt i = new CInt().set(allowedEntityEntries.size()).save(stream);
        CStringUTF8 str = new CStringUTF8();
        for (EntityEntry entry : allowedEntityEntries)
        {
            str.set(entry.getRegistryName().toString()).save(stream);
        }

        i.set(allowedEntityNames.size()).save(stream);
        for (CStringUTF8 name : allowedEntityNames)
        {
            name.save(stream);
        }
        return null;
    }

    @Override
    public CDialogueFilter load(FileInputStream stream) throws IOException
    {
        allowedEntityEntries.clear();
        CStringUTF8 str = new CStringUTF8();
        for (int i = new CInt().load(stream).value; i > 0; i--)
        {
            allowedEntityEntries.add(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(str.load(stream).value)));
        }

        allowedEntityNames.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--)
        {
            allowedEntityNames.add(new CStringUTF8().load(stream));
        }
        return null;
    }

    @Override
    public CDialogueFilter parse(String s)
    {
        return null;
    }

    @Override
    public CDialogueFilter copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen guiScreen)
    {
        return null;
    }

    @Override
    public CDialogueFilter setFromGUIElement(GUIElement guiElement)
    {
        return null;
    }
}

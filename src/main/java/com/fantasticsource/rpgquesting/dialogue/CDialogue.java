package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CDialogue extends Component
{
    public final CStringUTF8 name;

    public CDialogue(String name)
    {
        this.name = new CStringUTF8(this);
    }

    public boolean entityHas(Entity entity)
    {
        return false;
    }

    @Override
    public void write(ByteBuf byteBuf)
    {

    }

    @Override
    public void read(ByteBuf byteBuf)
    {

    }

    @Override
    public void save(FileOutputStream fileOutputStream) throws IOException
    {

    }

    @Override
    public void load(FileInputStream fileInputStream) throws IOException
    {

    }

    @Override
    public void parse(String s)
    {

    }

    @Override
    public Component copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement()
    {
        return null;
    }

    @Override
    public void setFromGUIElement(GUIElement guiElement)
    {

    }
}

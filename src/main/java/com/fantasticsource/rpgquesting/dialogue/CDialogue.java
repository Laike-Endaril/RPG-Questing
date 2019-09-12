package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CDialogue extends Component
{
    public CStringUTF8 name;
    public ArrayList<Component> mainContents = new ArrayList<>();
    public ArrayList<Component> buttons = new ArrayList<>();

    public CDialogue(String name)
    {
        this.name = new CStringUTF8(this).set(name);
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
    public GUIElement getGUIElement(GUIScreen screen)
    {
        return null;
    }

    @Override
    public void setFromGUIElement(GUIElement guiElement)
    {

    }
}

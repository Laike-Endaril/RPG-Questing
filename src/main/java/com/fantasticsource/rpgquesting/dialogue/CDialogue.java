package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.component.IObfuscatedComponent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CDialogue extends Component implements IObfuscatedComponent
{
    public CStringUTF8 name;
    public ArrayList<Component> mainContents = new ArrayList<>();
    public ArrayList<Component> buttons = new ArrayList<>();

    public CDialogue(String name)
    {
        this.name = new CStringUTF8().set(name);
    }

    public boolean entityHas(Entity entity)
    {
        return false;
    }

    @Override
    public CDialogue write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogue read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogue save(FileOutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogue load(FileInputStream fileInputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogue parse(String s)
    {
        return this;
    }

    @Override
    public CDialogue copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        return null;
    }

    @Override
    public CDialogue setFromGUIElement(GUIElement guiElement)
    {
        return this;
    }

    @Override
    public CDialogue writeObf(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogue readObf(ByteBuf byteBuf)
    {
        return this;
    }
}

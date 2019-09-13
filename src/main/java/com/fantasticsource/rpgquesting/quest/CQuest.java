package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CQuest extends Component
{
    public CStringUTF8 name, description;

    @Override
    public CQuest write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CQuest read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CQuest save(FileOutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CQuest load(FileInputStream fileInputStream) throws IOException
    {
        return this;
    }

    @Override
    public CQuest parse(String s)
    {
        return this;
    }

    @Override
    public CQuest copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        return null;
    }

    @Override
    public CQuest setFromGUIElement(GUIElement guiElement)
    {
        return this;
    }
}

package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CDialogueParagraph extends CStringUTF8
{
    @Override
    public CDialogueParagraph write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogueParagraph read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogueParagraph save(FileOutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogueParagraph load(FileInputStream fileInputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogueParagraph parse(String s)
    {
        return this;
    }

    @Override
    public CDialogueParagraph copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen guiScreen)
    {
        return null;
    }

    @Override
    public CDialogueParagraph setFromGUIElement(GUIElement guiElement)
    {
        return this;
    }
}

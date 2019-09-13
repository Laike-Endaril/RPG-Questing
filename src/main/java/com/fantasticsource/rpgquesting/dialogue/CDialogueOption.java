package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CInt;
import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CDialogueOption extends Component
{
    CStringUTF8 value;
    CInt nextDialogueSection;

    @Override
    public CDialogueOption write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogueOption read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogueOption save(FileOutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogueOption load(FileInputStream fileInputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogueOption parse(String s)
    {
        return this;
    }

    @Override
    public CDialogueOption copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen guiScreen)
    {
        return null;
    }

    @Override
    public CDialogueOption setFromGUIElement(GUIElement guiElement)
    {
        return this;
    }
}

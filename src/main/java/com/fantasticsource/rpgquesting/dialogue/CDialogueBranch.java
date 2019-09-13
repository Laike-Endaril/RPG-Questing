package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.component.IObfuscatedComponent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CDialogueBranch extends Component implements IObfuscatedComponent
{
    CDialogue parent = null;
    CStringUTF8 dialogueName = new CStringUTF8();
    CStringUTF8 paragraph = new CStringUTF8();
    ArrayList<CStringUTF8> choices = new ArrayList<>();

    public CDialogueBranch()
    {
        //Client-side only
    }

    public CDialogueBranch(CDialogue parent)
    {
        //Server-side only
        this.parent = parent;
        this.dialogueName = parent.displayName;
    }

    @Override
    public CDialogueBranch write(ByteBuf byteBuf)
    {
        return null;
    }

    @Override
    public CDialogueBranch read(ByteBuf byteBuf)
    {
        return null;
    }

    @Override
    public CDialogueBranch save(FileOutputStream fileOutputStream) throws IOException
    {
        return null;
    }

    @Override
    public CDialogueBranch load(FileInputStream fileInputStream) throws IOException
    {
        return null;
    }

    @Override
    public CDialogueBranch parse(String s)
    {
        return null;
    }

    @Override
    public CDialogueBranch copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen guiScreen)
    {
        return null;
    }

    @Override
    public CDialogueBranch setFromGUIElement(GUIElement guiElement)
    {
        return null;
    }

    @Override
    public CDialogueBranch writeObf(ByteBuf buf)
    {
        dialogueName.write(buf);
        paragraph.write(buf);
        buf.writeInt(choices.size());
        for (CStringUTF8 choice : choices) choice.write(buf);
        return null;
    }

    @Override
    public CDialogueBranch readObf(ByteBuf buf)
    {
        dialogueName.read(buf);
        paragraph.read(buf);
        for (int i = buf.readInt(); i > 0; i--) choices.add(new CStringUTF8().read(buf));
        return null;
    }
}

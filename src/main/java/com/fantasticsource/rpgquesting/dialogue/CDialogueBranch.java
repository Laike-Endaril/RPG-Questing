package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.CUUID;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.component.IObfuscatedComponent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CDialogueBranch extends Component implements IObfuscatedComponent
{
    CDialogue parent = null;
    public CUUID sessionID = new CUUID().set(UUID.randomUUID()), parentID = new CUUID();
    CStringUTF8 paragraph = new CStringUTF8();
    public ArrayList<CDialogueChoice> choices = new ArrayList<>();

    public CDialogueBranch()
    {
    }

    public CDialogueBranch(String paragraph, CDialogueChoice... choices)
    {
        this.paragraph.set(paragraph);
        this.choices.addAll(Arrays.asList(choices));
    }

    public CDialogueBranch setParent(CDialogue parent)
    {
        this.parent = parent;
        parentID = parent.sessionID;
        return this;
    }

    public CDialogueBranch add(CDialogueChoice... choices)
    {
        this.choices.addAll(Arrays.asList(choices));
        return this;
    }

    @Override
    public CDialogueBranch write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogueBranch read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogueBranch save(FileOutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogueBranch load(FileInputStream fileInputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogueBranch parse(String s)
    {
        return this;
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
        return this;
    }

    @Override
    public CDialogueBranch writeObf(ByteBuf buf)
    {
        parentID.write(buf);
        sessionID.write(buf);
        paragraph.write(buf);

        buf.writeInt(choices.size());
        for (CDialogueChoice choice : choices) choice.writeObf(buf);
        return this;
    }

    @Override
    public CDialogueBranch readObf(ByteBuf buf)
    {
        parentID.read(buf);

        sessionID.read(buf);
        paragraph.read(buf);

        for (int i = buf.readInt(); i > 0; i--) choices.add(new CDialogueChoice().readObf(buf));

        //This will only happen on server-side
        parent = Dialogues.get(parentID.value);
        if (parent != null)
        {
            for (CDialogueBranch branch : parent.branches)
            {
                if (branch.sessionID.equals(sessionID)) return branch;
            }
        }

        return this;
    }
}

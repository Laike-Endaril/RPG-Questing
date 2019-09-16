package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.tools.component.*;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CDialogueBranch extends Component implements IObfuscatedComponent
{
    public CUUID sessionID = new CUUID().set(UUID.randomUUID()), parentSessionID = new CUUID();
    public CDialogue parent = null;

    public ArrayList<CDialogueChoice> choices = new ArrayList<>();
    CStringUTF8 paragraph = new CStringUTF8();

    public CDialogueBranch()
    {
    }

    public CDialogueBranch(String paragraph)
    {
        this.paragraph.set(paragraph);
    }

    public CDialogueBranch setParent(CDialogue parent)
    {
        this.parent = parent;
        parentSessionID = parent.sessionID;
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
    public CDialogueBranch save(OutputStream stream) throws IOException
    {
        parent.permanentID.save(stream);
        paragraph.save(stream);

        new CInt().set(choices.size()).save(stream);
        for (CDialogueChoice choice : choices) choice.save(stream);

        return this;
    }

    @Override
    public CDialogueBranch load(InputStream stream) throws IOException
    {
        setParent(CDialogues.getByPermanentID(new CUUID().load(stream).value));
        paragraph.load(stream);

        choices.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) choices.add(new CDialogueChoice().load(stream));

        return this;
    }

    @Override
    public CDialogueBranch writeObf(ByteBuf buf)
    {
        parentSessionID.write(buf);
        sessionID.write(buf);
        paragraph.write(buf);

        buf.writeInt(choices.size());
        for (CDialogueChoice choice : choices) choice.writeObf(buf);
        return this;
    }

    @Override
    public CDialogueBranch readObf(ByteBuf buf)
    {
        parentSessionID.read(buf);

        sessionID.read(buf);
        paragraph.read(buf);

        for (int i = buf.readInt(); i > 0; i--) choices.add(new CDialogueChoice().readObf(buf));

        //This will only happen on server-side
        parent = CDialogues.getBySessionID(parentSessionID.value);
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

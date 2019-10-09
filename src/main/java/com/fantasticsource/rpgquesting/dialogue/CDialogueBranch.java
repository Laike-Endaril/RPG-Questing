package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.tools.component.*;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CDialogueBranch extends Component implements IObfuscatedComponent
{
    public CUUID sessionID = new CUUID().set(UUID.randomUUID()), dialogueSessionID = new CUUID();
    public CDialogue dialogue = null;

    public ArrayList<CDialogueChoice> choices = new ArrayList<>();
    public CStringUTF8 paragraph = new CStringUTF8();

    public CDialogueBranch()
    {
    }

    public CDialogueBranch(String paragraph)
    {
        this.paragraph.set(paragraph);
    }

    public CDialogueBranch setDialogue(CDialogue dialogue)
    {
        this.dialogue = dialogue;
        dialogueSessionID = dialogue.sessionID;
        return this;
    }

    public CDialogueBranch add(CDialogueChoice... choices)
    {
        this.choices.addAll(Arrays.asList(choices));
        return this;
    }

    @Override
    public CDialogueBranch write(ByteBuf buf)
    {
        dialogue.permanentID.write(buf);
        paragraph.write(buf);

        new CInt().set(choices.size()).write(buf);
        for (CDialogueChoice choice : choices) choice.write(buf);

        return this;
    }

    @Override
    public CDialogueBranch read(ByteBuf buf)
    {
        setDialogue(CDialogues.getByPermanentID(new CUUID().read(buf).value));
        paragraph.read(buf);

        choices.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--) choices.add(new CDialogueChoice().read(buf));

        return this;
    }

    @Override
    public CDialogueBranch save(OutputStream stream)
    {
        dialogue.permanentID.save(stream);
        paragraph.save(stream);

        new CInt().set(choices.size()).save(stream);
        for (CDialogueChoice choice : choices) choice.save(stream);

        return this;
    }

    @Override
    public CDialogueBranch load(InputStream stream)
    {
        setDialogue(CDialogues.getByPermanentID(new CUUID().load(stream).value));
        paragraph.load(stream);

        choices.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) choices.add(new CDialogueChoice().load(stream));

        return this;
    }

    @Override
    public CDialogueBranch writeObf(ByteBuf buf)
    {
        dialogueSessionID.write(buf);
        sessionID.write(buf);
        paragraph.write(buf);

        buf.writeInt(choices.size());
        for (CDialogueChoice choice : choices) choice.writeObf(buf);
        return this;
    }

    @Override
    public CDialogueBranch readObf(ByteBuf buf)
    {
        dialogueSessionID.read(buf);

        sessionID.read(buf);
        paragraph.read(buf);

        for (int i = buf.readInt(); i > 0; i--) choices.add(new CDialogueChoice().readObf(buf));

        //This will only happen on server-side
        dialogue = CDialogues.getBySessionID(dialogueSessionID.value);
        if (dialogue != null)
        {
            for (CDialogueBranch branch : dialogue.branches)
            {
                if (branch.sessionID.equals(sessionID)) return branch;
            }
        }

        return this;
    }
}

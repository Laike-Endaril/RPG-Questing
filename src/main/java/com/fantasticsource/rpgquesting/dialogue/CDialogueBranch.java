package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class CDialogueBranch extends Component
{
    public CStringUTF8 dialogueName = new CStringUTF8();

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
        this.dialogueName.set(dialogue.name.value);
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
        return this;
    }

    @Override
    public CDialogueBranch read(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CDialogueBranch save(OutputStream stream)
    {
        paragraph.save(stream);

        new CInt().set(choices.size()).save(stream);
        for (CDialogueChoice choice : choices) choice.save(stream);

        return this;
    }

    @Override
    public CDialogueBranch load(InputStream stream)
    {
        paragraph.load(stream);

        choices.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) choices.add(new CDialogueChoice().load(stream));

        return this;
    }
}

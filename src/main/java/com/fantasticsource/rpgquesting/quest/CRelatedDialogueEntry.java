package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CRelatedDialogueEntry extends Component
{
    public static final int
            TYPE_BRANCH = 0,
            TYPE_PLAYER_CONDITION = 1,
            TYPE_ENTITY_CONDITION = 2;


    public CStringUTF8 dialogueName = new CStringUTF8(), relation = new CStringUTF8();
    public CInt type = new CInt(), index = new CInt();

    public CRelatedDialogueEntry()
    {
    }

    public CRelatedDialogueEntry(String dialogueName, int type, int index, String relation)
    {
        this.dialogueName.set(dialogueName);
        this.type.set(type);
        this.index.set(index);
        this.relation.set(relation);
    }

    public String getSource()
    {
        switch (type.value)
        {
            case TYPE_BRANCH:
                return "Branch " + index.value;

            case TYPE_PLAYER_CONDITION:
                return "Player Condition " + index.value;

            case TYPE_ENTITY_CONDITION:
                return "Entity Condition " + index.value;
        }

        return null;
    }

    @Override
    public CRelatedDialogueEntry write(ByteBuf buf)
    {
        dialogueName.write(buf);
        relation.write(buf);
        type.write(buf);
        index.write(buf);

        return this;
    }

    @Override
    public CRelatedDialogueEntry read(ByteBuf buf)
    {
        dialogueName.read(buf);
        relation.read(buf);
        type.read(buf);
        index.read(buf);

        return this;
    }

    @Override
    public CRelatedDialogueEntry save(OutputStream stream)
    {
        dialogueName.save(stream);
        relation.save(stream);
        type.save(stream);
        index.save(stream);

        return this;
    }

    @Override
    public CRelatedDialogueEntry load(InputStream stream)
    {
        dialogueName.load(stream);
        relation.load(stream);
        type.load(stream);
        index.load(stream);

        return this;
    }
}

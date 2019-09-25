package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.CUUID;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CQuestCondition extends CCondition
{
    public CStringUTF8 name = new CStringUTF8();
    public CUUID dialogueID = new CUUID();
    public CInt branchIndex = new CInt();


    public CQuestCondition()
    {
    }

    public CQuestCondition(CQuest quest)
    {
        this(quest.name.value);
    }

    public CQuestCondition(String name) //TODO Need to go through usages and add dialogue/branch ref
    {
        this.name.set(name);
    }


    @Override
    public CQuestCondition write(ByteBuf buf)
    {
        name.write(buf);
        dialogueID.write(buf);
        branchIndex.write(buf);

        return this;
    }

    @Override
    public CQuestCondition read(ByteBuf buf)
    {
        name.read(buf);
        dialogueID.read(buf);
        branchIndex.read(buf);

        return this;
    }

    @Override
    public CQuestCondition save(OutputStream stream)
    {
        name.save(stream);
        dialogueID.save(stream);
        branchIndex.save(stream);

        return this;
    }

    @Override
    public CQuestCondition load(InputStream stream)
    {
        name.load(stream);
        dialogueID.load(stream);
        branchIndex.load(stream);

        return this;
    }
}

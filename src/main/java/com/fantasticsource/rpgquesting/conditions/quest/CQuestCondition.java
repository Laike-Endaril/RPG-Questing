package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.CUUID;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public abstract class CQuestCondition extends CCondition
{
    public CStringUTF8 name = new CStringUTF8();
    public CUUID dialogueID = new CUUID().set(UUID.randomUUID());
    public CInt branchIndex = new CInt().set(-1);


    public CQuestCondition()
    {
    }

    public CQuestCondition(CQuest quest, CDialogueBranch branch)
    {
        name.set(quest.name.value);

        if (branch != null)
        {
            dialogueID.set(branch.dialogue.permanentID.value);
            branchIndex.set(branch.dialogue.branches.indexOf(branch));
        }
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

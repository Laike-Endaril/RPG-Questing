package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CRelatedDialogueEntry;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CQuestCondition extends CCondition
{
    public CStringUTF8 name = new CStringUTF8();
    public CStringUTF8 dialogueName = new CStringUTF8();


    public CQuestCondition()
    {
    }

    public CQuestCondition(CQuest quest, CDialogueBranch branch)
    {
        name.set(quest.name.value);

        if (branch != null)
        {
            dialogueName.set(branch.dialogueName.value);

            quest.relatedDialogues.add(new CRelatedDialogueEntry(branch, relation()));
        }
    }


    public abstract String relation();


    @Override
    public CQuestCondition write(ByteBuf buf)
    {
        name.write(buf);
        dialogueName.write(buf);

        return this;
    }

    @Override
    public CQuestCondition read(ByteBuf buf)
    {
        name.read(buf);
        dialogueName.read(buf);

        return this;
    }

    @Override
    public CQuestCondition save(OutputStream stream)
    {
        name.save(stream);
        dialogueName.save(stream);

        return this;
    }

    @Override
    public CQuestCondition load(InputStream stream)
    {
        name.load(stream);
        dialogueName.load(stream);

        return this;
    }
}

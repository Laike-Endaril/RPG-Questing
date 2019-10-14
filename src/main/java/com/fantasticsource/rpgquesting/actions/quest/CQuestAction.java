package com.fantasticsource.rpgquesting.actions.quest;

import com.fantasticsource.rpgquesting.actions.CAction;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CRelatedDialogueEntry;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CQuestAction extends CAction
{
    public CStringUTF8 questName = new CStringUTF8();


    public CQuestAction()
    {
    }

    public CQuestAction(CQuest quest, CDialogueBranch branch)
    {
        questName.set(quest.name.value);

        if (branch != null)
        {
            quest.relatedDialogues.add(new CRelatedDialogueEntry(branch, relation()));
        }
    }


    public abstract String relation();


    @Override
    public CQuestAction write(ByteBuf buf)
    {
        questName.write(buf);

        buf.writeInt(conditions.size());
        for (CCondition condition : conditions) Component.writeMarked(buf, condition);

        return this;
    }

    @Override
    public CQuestAction read(ByteBuf buf)
    {
        questName.read(buf);

        conditions.clear();
        for (int i = buf.readInt(); i > 0; i--) conditions.add((CCondition) Component.readMarked(buf));

        return this;
    }

    @Override
    public CQuestAction save(OutputStream stream)
    {
        questName.save(stream);

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CQuestAction load(InputStream stream)
    {
        questName.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }
}

package com.fantasticsource.rpgquesting.actions.quest;

import com.fantasticsource.rpgquesting.actions.CAction;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.quest.CRelatedDialogueEntry;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CQuestAction extends CAction
{
    public CStringUTF8 questName = new CStringUTF8();


    public CQuestAction()
    {
    }

    public CQuestAction(CQuest quest)
    {
        questName.set(quest.name.value);
    }


    public abstract String relation();


    @Override
    public void updateRelations(String dialogueName, int type, int index)
    {
        CQuest quest = CQuests.get(questName.value);
        if (quest != null)
        {
            CDialogue dialogue = CDialogues.get(dialogueName);
            if (dialogue != null)
            {
                CRelatedDialogueEntry newEntry = new CRelatedDialogueEntry(dialogueName, type, index, relation());

                boolean found = false;
                for (CRelatedDialogueEntry entry : quest.relatedDialogues)
                {
                    if (entry.index.value == newEntry.index.value && entry.dialogueName.value.equals(newEntry.dialogueName.value) && entry.relation.value.equals(newEntry.relation.value))
                    {
                        found = true;
                        break;
                    }
                }

                if (!found) quest.relatedDialogues.add(newEntry);
            }
        }
    }


    @Override
    public CQuestAction write(ByteBuf buf)
    {
        super.write(buf);
        questName.write(buf);
        return this;
    }

    @Override
    public CQuestAction read(ByteBuf buf)
    {
        super.read(buf);
        questName.read(buf);
        return this;
    }

    @Override
    public CQuestAction save(OutputStream stream)
    {
        super.save(stream);
        questName.save(stream);
        return this;
    }

    @Override
    public CQuestAction load(InputStream stream)
    {
        super.load(stream);
        questName.load(stream);
        return this;
    }
}

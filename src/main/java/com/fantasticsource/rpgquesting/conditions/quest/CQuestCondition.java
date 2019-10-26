package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.quest.CRelatedDialogueEntry;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CQuestCondition extends CCondition
{
    public CStringUTF8 questName = new CStringUTF8();


    public CQuestCondition()
    {
    }

    public CQuestCondition(CQuest quest)
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
    public CQuestCondition write(ByteBuf buf)
    {
        questName.write(buf);
        return this;
    }

    @Override
    public CQuestCondition read(ByteBuf buf)
    {
        questName.read(buf);
        return this;
    }

    @Override
    public CQuestCondition save(OutputStream stream)
    {
        questName.save(stream);
        return this;
    }

    @Override
    public CQuestCondition load(InputStream stream)
    {
        questName.load(stream);
        return this;
    }
}

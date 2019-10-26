package com.fantasticsource.rpgquesting.actions.quest;

import com.fantasticsource.rpgquesting.actions.CAction;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.quest.CRelatedDialogueEntry;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CQuestAction extends CAction
{
    public CStringUTF8 questName = new CStringUTF8(), dialogueName = new CStringUTF8().set("");
    public CInt branchIndex = new CInt();


    public CQuestAction()
    {
    }

    public CQuestAction(CQuest quest, CDialogueBranch branch)
    {
        questName.set(quest.name.value);

        if (branch != null)
        {
            dialogueName.set(branch.dialogueName.value);
            branchIndex.set(CDialogues.get(dialogueName.value).branches.indexOf(branch));

            quest.relatedDialogues.add(new CRelatedDialogueEntry(branch, relation()));
        }
    }


    public abstract String relation();


    @Override
    public void updateRelations()
    {
        CQuest quest = CQuests.get(questName.value);
        if (quest != null)
        {
            CDialogue dialogue = CDialogues.get(dialogueName.value);
            if (dialogue != null && branchIndex.value >= 0 && branchIndex.value < dialogue.branches.size())
            {
                CDialogueBranch branch = dialogue.branches.get(branchIndex.value);
                CRelatedDialogueEntry newEntry = new CRelatedDialogueEntry(branch, relation());

                boolean found = false;
                for (CRelatedDialogueEntry entry : quest.relatedDialogues)
                {
                    if (entry.branchIndex.value == newEntry.branchIndex.value && entry.dialogueName.value.equals(newEntry.dialogueName.value) && entry.relation.value.equals(newEntry.relation.value))
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
    public void setDialogueName(String name)
    {
        dialogueName.set(name);
    }

    @Override
    public CQuestAction write(ByteBuf buf)
    {
        super.write(buf);

        questName.write(buf);
        dialogueName.write(buf);
        branchIndex.write(buf);

        return this;
    }

    @Override
    public CQuestAction read(ByteBuf buf)
    {
        super.read(buf);

        questName.read(buf);
        dialogueName.read(buf);
        branchIndex.read(buf);

        return this;
    }

    @Override
    public CQuestAction save(OutputStream stream)
    {
        super.save(stream);

        questName.save(stream);
        dialogueName.save(stream);
        branchIndex.save(stream);

        return this;
    }

    @Override
    public CQuestAction load(InputStream stream)
    {
        super.load(stream);

        questName.load(stream);
        dialogueName.load(stream);
        branchIndex.load(stream);

        return this;
    }
}

package com.fantasticsource.rpgquesting.actions.quest;

import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;

public class CActionCompleteQuest extends CQuestAction
{
    public CActionCompleteQuest()
    {
        super();
    }

    public CActionCompleteQuest(CQuest quest, CDialogueBranch branch)
    {
        super(quest, branch);
    }

    @Override
    public String relation()
    {
        return "completes this quest";
    }
}

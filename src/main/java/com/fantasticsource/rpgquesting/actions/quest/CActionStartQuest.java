package com.fantasticsource.rpgquesting.actions.quest;

import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;

public class CActionStartQuest extends CQuestAction
{
    public CActionStartQuest()
    {
        super();
    }

    public CActionStartQuest(CQuest quest, CDialogueBranch branch)
    {
        super(quest, branch);
    }

    @Override
    public String relation()
    {
        return "Starts quest:";
    }
}

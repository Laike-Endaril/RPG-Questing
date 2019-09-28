package com.fantasticsource.rpgquesting.actions.quest;

import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

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
        return "starts this quest";
    }

    @Override
    protected void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        CQuests.start((EntityPlayerMP) entity, name.value);
    }

    @Override
    public String description()
    {
        return "Starts quest: " + name.value;
    }
}

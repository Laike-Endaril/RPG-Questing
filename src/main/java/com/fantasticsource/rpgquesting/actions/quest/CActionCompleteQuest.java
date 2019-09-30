package com.fantasticsource.rpgquesting.actions.quest;

import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;

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

    @Override
    protected void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        CQuests.complete((EntityPlayerMP) entity, name.value);
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Complete quest: " + name.value);
        return result;
    }
}

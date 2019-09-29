package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.selectionguis.GUICondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class CConditionQuestReadyToComplete extends CQuestCondition
{
    public CConditionQuestReadyToComplete()
    {
        super();
    }

    public CConditionQuestReadyToComplete(CQuest quest, CDialogueBranch branch)
    {
        super(quest, branch);
    }

    @Override
    public String relation()
    {
        return "requires that this quest be ready to turn in";
    }


    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            CQuest quest = CQuests.get(name.value);
            if (quest == null) result.add("Quest must be ready to be completed (quest does not exist!): \"" + name + '"');
            else if (!quest.isReadyToComplete((EntityPlayerMP) entity)) result.add("Quest must be ready to be completed: \"" + quest.name.value + '"');
        }
        return result;
    }

    @Override
    public String description()
    {
        return "Requires quest be ready to turn in: " + TextFormatting.GOLD + name.value;
    }

    @Override
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        CQuest quest = new CQuest();
        quest.name.set("Quest Name");
        return new GUICondition(screen, new CConditionQuestReadyToComplete(quest, null));
    }
}

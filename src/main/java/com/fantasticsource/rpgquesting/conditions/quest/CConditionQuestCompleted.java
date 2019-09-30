package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.conditions.gui.GUICondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class CConditionQuestCompleted extends CQuestCondition
{
    public CConditionQuestCompleted()
    {
        super();
    }

    public CConditionQuestCompleted(CQuest quest, CDialogueBranch branch)
    {
        super(quest, branch);
    }

    @Override
    public String relation()
    {
        return "requires that this quest be completed";
    }


    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            CQuest quest = CQuests.get(name.value);
            if (quest == null) result.add("Quest must be completed (quest does not exist!): \"" + name.value + '"');
            else if (!quest.isCompleted((EntityPlayerMP) entity)) result.add("Quest must be completed: \"" + quest.name.value + '"');
        }
        return result;
    }

    @Override
    public String description()
    {
        return "Requires quest be completed: " + TextFormatting.GOLD + name.value;
    }

    @Override
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        CQuest quest = new CQuest();
        quest.name.set("Quest Name");
        return new GUICondition(screen, new CConditionQuestCompleted(quest, null));
    }
}

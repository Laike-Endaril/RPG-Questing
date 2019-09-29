package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.selectionguis.GUICondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class CConditionQuestAvailable extends CQuestCondition
{
    public CConditionQuestAvailable()
    {
        super();
    }

    public CConditionQuestAvailable(CQuest quest, CDialogueBranch branch)
    {
        super(quest, branch);
    }

    @Override
    public String relation()
    {
        return "requires that this quest be available to start";
    }


    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            CQuest quest = CQuests.get(name.value);
            if (quest == null) result.add("Quest must be available (quest does not exist!): \"" + name.value + '"');
            else if (!quest.isAvailable((EntityPlayerMP) entity)) result.add("Quest must be available: \"" + quest.name.value + '"');
        }
        return result;
    }

    @Override
    public String description()
    {
        return "Requires quest be available: " + TextFormatting.GOLD + name.value;
    }

    @Override
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        CQuest quest = new CQuest();
        quest.name.set("Quest Name");
        return new GUICondition(screen, new CConditionQuestAvailable(quest, null));
    }

    @Override
    public GUIElement getEditableElement(GUIScreen screen)
    {
        //TODO
        return null;
    }
}

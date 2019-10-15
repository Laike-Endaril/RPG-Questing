package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.gui.GUICondition;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class CConditionQuestInProgress extends CQuestCondition
{
    public CConditionQuestInProgress()
    {
        super();
    }

    public CConditionQuestInProgress(CQuest quest, CDialogueBranch branch)
    {
        super(quest, branch);
    }

    @Override
    public String relation()
    {
        return "requires that this quest be in progress (not ready to turn in)";
    }


    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            CQuest quest = CQuests.get(name.value);
            if (quest == null) result.add("Quest must be in progress (quest does not exist!): \"" + name.value + '"');
            else if (!quest.isInProgress((EntityPlayerMP) entity)) result.add("Quest must be in progress (not ready to turn in): \"" + quest.name.value + '"');
        }
        return result;
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Quest in progress (not ready to turn in): " + name.value);
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        CQuest quest = new CQuest();
        quest.name.set("Quest Name");
        return new GUICondition(screen, new CConditionQuestInProgress(quest, null));
    }
}

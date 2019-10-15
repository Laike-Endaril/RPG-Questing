package com.fantasticsource.rpgquesting.actions.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.gui.GUIAction;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        CQuests.complete((EntityPlayerMP) entity, questName.value);
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Complete quest: " + questName.value);
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIAction getChoosableElement(GUIScreen screen)
    {
        CQuest quest = new CQuest();
        quest.name.set("Quest Name");
        return new GUIAction(screen, new CActionCompleteQuest(quest, null));
    }
}

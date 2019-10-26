package com.fantasticsource.rpgquesting.actions.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUIAction;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class CActionStartQuest extends CQuestAction
{
    public CActionStartQuest()
    {
        super();
    }

    public CActionStartQuest(CQuest quest)
    {
        super(quest);
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
        CQuests.start((EntityPlayerMP) entity, questName.value);
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Start quest: " + questName.value);
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIAction getChoosableElement(GUIScreen screen)
    {
        CQuest quest = new CQuest();
        quest.name.set("Quest Name");
        return new GUIAction(screen, new CActionStartQuest(quest));
    }
}

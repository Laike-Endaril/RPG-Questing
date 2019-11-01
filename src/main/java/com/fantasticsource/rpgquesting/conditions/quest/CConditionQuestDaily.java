package com.fantasticsource.rpgquesting.conditions.quest;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUICondition;
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuestTimestamp;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CInt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.time.Instant;
import java.util.ArrayList;

public class CConditionQuestDaily extends CQuestCondition
{
    public CInt hourOffset = new CInt();

    public CConditionQuestDaily()
    {
        super();
    }

    public CConditionQuestDaily(CQuest quest, int hourOffset)
    {
        super(quest);
        this.hourOffset.set(hourOffset % 24);
    }

    @Override
    public String relation()
    {
        return "requires that this quest not be completed yet today";
    }


    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            CPlayerQuestData data = CQuests.playerQuestData.get(entity.getPersistentID());
            if (data == null) return result;

            CQuestTimestamp timestamp = data.startData.get(questName.value);
            if (timestamp == null) return result;

            Instant instant = timestamp.timestamp.value.getInstant();
            if ((instant.toEpochMilli() - 3600000 * hourOffset.value) < ((Instant.now().toEpochMilli() - 3600000 * hourOffset.value) % 172800000)) return result;

            CQuest quest = CQuests.get(questName.value);
            if (quest == null) result.add("Quest must not yet be completed today (quest does not exist!): \"" + questName.value + '"');
            else if (quest.isCompleted((EntityPlayerMP) entity)) result.add("Quest must not yet be completed today: \"" + quest.name.value + '"');
            if (result.size() > 0) System.out.println(result.get(result.size() - 1));
        }
        return result;
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Quest not yet completed today: " + questName.value);
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        CQuest quest = new CQuest();
        quest.name.set("Quest Name");
        return new GUICondition(screen, new CConditionQuestDaily(quest, 0));
    }
}

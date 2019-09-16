package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.actors.threadpool.Arrays;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CObjectiveKill extends CObjective
{
    CInt current = new CInt(), required = new CInt();
    ArrayList<CCondition> conditions = new ArrayList<>();

    public CObjectiveKill()
    {
    }

    public CObjectiveKill(int killsRequired, CCondition... conditions)
    {
        this.required.set(killsRequired);
        this.conditions.addAll(Arrays.asList(conditions));
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event)
    {
        Entity entity = event.getEntity();
        for (CPlayerQuestData data : CQuests.playerQuestData.values())
        {
            for (ArrayList<CObjective> objectives : data.inProgressQuests.values())
            {
                for (CObjective objective : objectives)
                {
                    if (objective.getClass() == CObjectiveKill.class)
                    {
                        CObjectiveKill objectiveKill = (CObjectiveKill) objective;
                        if (objectiveKill.current.value < objectiveKill.required.value)
                        {
                            boolean doit = true;
                            for (CCondition condition : objectiveKill.conditions)
                            {
                                if (condition.unmetConditions(entity).size() > 0)
                                {
                                    doit = false;
                                    break;
                                }
                            }

                            if (doit)
                            {
                                objectiveKill.current.value++;
                                //TODO debug message
                                System.out.println(objectiveKill.current.value + "/" + objectiveKill.required.value);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected String progressText()
    {
        if (required.value > 1) return current.value + "/" + required.value;
        else if (current.value == 1) return "[x]";
        else return "[ ]";
    }

    @Override
    public boolean isDone()
    {
        return current.value >= required.value;
    }

    @Override
    public CObjectiveKill write(ByteBuf buf)
    {
        owner.write(buf);
        text.write(buf);
        current.write(buf);
        required.write(buf);
        return this;
    }

    @Override
    public CObjectiveKill read(ByteBuf buf)
    {
        owner.read(buf);
        text.read(buf);
        current.read(buf);
        required.read(buf);
        return this;
    }

    @Override
    public CObjectiveKill save(OutputStream stream) throws IOException
    {
        new CBoolean().set(owner.value != null).save(stream);
        if (owner.value != null) owner.save(stream);
        text.save(stream);
        current.save(stream);
        required.save(stream);
        return this;
    }

    @Override
    public CObjectiveKill load(InputStream stream) throws IOException
    {
        if (new CBoolean().load(stream).value) owner.load(stream);
        text.load(stream);
        current.load(stream);
        required.load(stream);
        return this;
    }
}

package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.gui.GUIObjective;
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.CombatTracker;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class CObjectiveKill extends CObjective
{
    static
    {
        MinecraftForge.EVENT_BUS.register(CObjectiveKill.class);
    }

    public CInt required = new CInt();
    public ArrayList<CCondition> conditions = new ArrayList<>();
    CInt current = new CInt();

    public CObjectiveKill()
    {
    }

    public CObjectiveKill(String text, int killsRequired, CCondition... conditions)
    {
        this.text.set(text);
        this.required.set(killsRequired);
        this.conditions.addAll(Arrays.asList(conditions));
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event)
    {
        Entity killer;
        Entity entity = event.getEntity();

        if (!(entity instanceof EntityLivingBase)) return;
        {
            CombatTracker tracker = ((EntityLivingBase) entity).getCombatTracker();
            killer = tracker.getBestAttacker();
            if (!(killer instanceof EntityPlayerMP)) return;
        }
        EntityPlayerMP player = (EntityPlayerMP) killer;

        CPlayerQuestData data = CQuests.playerQuestData.get(player.getPersistentID());
        if (data == null) return;

        for (LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> map : data.inProgressQuests.values())
        {
            for (Pair<CUUID, ArrayList<CObjective>> value : map.values())
            {
                ArrayList<CObjective> objectives = value.getValue();

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
                                data.saveAndSync();
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
        if (required.value > 1) return "(" + current.value + "/" + required.value + ")";
        else if (current.value == 1) return "[x]";
        else return "[ ]";
    }

    @Override
    public boolean isStarted()
    {
        return isDone() || current.value > 0;
    }

    @Override
    public boolean isDone()
    {
        return current.value >= required.value;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIObjective getChoosableElement(GUIScreen screen)
    {
        GUIObjective guiObjective = new GUIObjective(screen, new CObjectiveKill("Entities killed", 1));
        guiObjective.text = guiObjective.text.replace("[ ]", "(?/?)");
        return guiObjective;
    }

    @Override
    public CObjectiveKill write(ByteBuf buf)
    {
        super.write(buf);

        current.write(buf);
        required.write(buf);

        buf.writeInt(conditions.size());
        for (CCondition condition : conditions) Component.writeMarked(buf, condition);

        return this;
    }

    @Override
    public CObjectiveKill read(ByteBuf buf)
    {
        super.read(buf);

        current.read(buf);
        required.read(buf);

        conditions.clear();
        for (int i = buf.readInt(); i > 0; i--) conditions.add((CCondition) Component.readMarked(buf));

        return this;
    }

    @Override
    public CObjectiveKill save(OutputStream stream)
    {
        super.save(stream);

        current.save(stream);
        required.save(stream);

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CObjectiveKill load(InputStream stream)
    {
        super.load(stream);

        current.load(stream);
        required.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }

    @Override
    public CObjectiveKill writeObf(ByteBuf buf)
    {
        super.writeObf(buf);

        current.write(buf);
        required.write(buf);

        return this;
    }

    @Override
    public CObjectiveKill readObf(ByteBuf buf)
    {
        super.readObf(buf);

        current.read(buf);
        required.read(buf);

        return this;
    }
}

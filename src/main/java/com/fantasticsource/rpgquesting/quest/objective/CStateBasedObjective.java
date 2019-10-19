package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class CStateBasedObjective extends CObjective
{
    static
    {
        MinecraftForge.EVENT_BUS.register(CStateBasedObjective.class);
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == Side.CLIENT || event.phase != TickEvent.Phase.END) return;

        FMLCommonHandler.instance().getMinecraftServerInstance().profiler.startSection("RPG Questing: State-based objective checks");
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        CPlayerQuestData data = CQuests.playerQuestData.get(player.getPersistentID());
        if (data == null) return;

        boolean changed = false, includesTracked = false;
        for (LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> quests : data.inProgressQuests.values())
        {
            for (Map.Entry<String, Pair<CUUID, ArrayList<CObjective>>> quest : quests.entrySet())
            {
                ArrayList<CObjective> objectives = quest.getValue().getValue();

                for (CObjective objective : objectives)
                {
                    if (objective instanceof CStateBasedObjective)
                    {
                        changed |= ((CStateBasedObjective) objective).check(player);
                        includesTracked |= data.trackedQuestName.value.equals(quest.getKey());
                    }
                }
            }
        }

        if (changed) data.saveAndSync();
        else if (includesTracked) CQuests.syncTracker(player);

        FMLCommonHandler.instance().getMinecraftServerInstance().profiler.endSection();
    }

    public abstract boolean check(EntityPlayerMP player);
}

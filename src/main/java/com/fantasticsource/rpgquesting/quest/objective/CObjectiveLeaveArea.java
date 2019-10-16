package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUIObjective;
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.datastructures.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CObjectiveLeaveArea extends CObjectiveArea
{
    static
    {
        MinecraftForge.EVENT_BUS.register(CObjectiveLeaveArea.class);
    }

    public final CBoolean done = new CBoolean();

    public CObjectiveLeaveArea()
    {
        super();
    }

    public CObjectiveLeaveArea(String text, int x1, int y1, int z1, int x2, int y2, int z2)
    {
        super(text, x1, y1, z1, x2, y2, z2);
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == Side.CLIENT || event.phase != TickEvent.Phase.END || ServerTickTimer.currentTick() % 10 != 0) return;


        EntityPlayerMP player = (EntityPlayerMP) event.player;

        CPlayerQuestData data = CQuests.playerQuestData.get(player.getPersistentID());
        if (data == null) return;

        boolean changed = false;
        for (LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> map : data.inProgressQuests.values())
        {
            for (Pair<CUUID, ArrayList<CObjective>> value : map.values())
            {
                ArrayList<CObjective> objectives = value.getValue();

                for (CObjective objective : objectives)
                {
                    if (objective.getClass() == CObjectiveLeaveArea.class)
                    {
                        CObjectiveLeaveArea objectiveEnterArea = (CObjectiveLeaveArea) objective;
                        BlockPos pos = player.getPosition();
                        CInt[] coords = objectiveEnterArea.coords;

                        if (!objectiveEnterArea.done.value
                                && (pos.getX() < coords[0].value || pos.getX() > coords[3].value
                                || pos.getY() < coords[1].value || pos.getY() > coords[4].value
                                || pos.getZ() < coords[2].value || pos.getZ() > coords[5].value))
                        {
                            objectiveEnterArea.done.set(true);
                            changed = true;
                        }
                    }
                }
            }
        }

        if (changed) data.saveAndSync();
    }

    @Override
    protected String progressText()
    {
        return done.value ? "[x]" : "[ ]";
    }

    @Override
    public boolean isStarted()
    {
        return isDone();
    }

    @Override
    public boolean isDone()
    {
        return done.value;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIObjective getChoosableElement(GUIScreen screen)
    {
        return new GUIObjective(screen, new CObjectiveLeaveArea("Leave area", 0, 0, 0, 1, 1, 1));
    }

    @Override
    public CObjectiveLeaveArea write(ByteBuf buf)
    {
        super.write(buf);

        done.write(buf);

        return this;
    }

    @Override
    public CObjectiveLeaveArea read(ByteBuf buf)
    {
        super.read(buf);

        done.read(buf);

        return this;
    }

    @Override
    public CObjectiveLeaveArea save(OutputStream stream)
    {
        super.save(stream);

        done.save(stream);

        return this;
    }

    @Override
    public CObjectiveLeaveArea load(InputStream stream)
    {
        super.load(stream);

        done.load(stream);

        return this;
    }

    @Override
    public CObjectiveLeaveArea writeObf(ByteBuf buf)
    {
        super.writeObf(buf);

        done.write(buf);

        return this;
    }

    @Override
    public CObjectiveLeaveArea readObf(ByteBuf buf)
    {
        super.readObf(buf);

        done.read(buf);

        return this;
    }
}

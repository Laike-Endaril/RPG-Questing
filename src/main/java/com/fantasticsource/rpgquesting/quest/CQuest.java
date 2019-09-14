package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.tools.component.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CQuest extends Component
{
    public CUUID permanentID = new CUUID();

    public ArrayList<CCondition> conditions = new ArrayList<>();

    public CStringUTF8 name = new CStringUTF8();
    public CInt level = new CInt();
    public ArrayList<CObjective> objectives = new ArrayList<>();
    public CBoolean repeatable = new CBoolean();

    public CInt experience = new CInt();
    public ArrayList<CItemStack> rewards = new ArrayList<>();


    public final boolean isAvailable(EntityPlayerMP player)
    {
        if (isInProgress(player)) return false;

        if (!repeatable.value && isComplete(player)) return false;

        for (CCondition condition : conditions) if (!condition.check(player)) return false;
        return true;
    }

    public final boolean isInProgress(EntityPlayerMP player)
    {
        //TODO
    }

    public final boolean isComplete(EntityPlayerMP player)
    {
        //TODO
    }

    @Override
    public CQuest write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CQuest read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CQuest save(OutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CQuest load(InputStream fileInputStream) throws IOException
    {
        return this;
    }
}

package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.CloseDialoguePacket;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.InputStream;
import java.io.OutputStream;

public class CActionEndDialogue extends CAction
{
    @Override
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        Network.WRAPPER.sendTo(new CloseDialoguePacket(), (EntityPlayerMP) entity);
    }

    @Override
    public String description()
    {
        return "Ends / closes the current dialogue";
    }

    @Override
    public CActionEndDialogue write(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionEndDialogue read(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionEndDialogue save(OutputStream stream)
    {
        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CActionEndDialogue load(InputStream stream)
    {
        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }
}

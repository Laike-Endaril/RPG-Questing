package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.CloseDialoguePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CActionEndDialogue extends CAction
{
    @Override
    public void execute(EntityPlayerMP player)
    {
        Network.WRAPPER.sendTo(new CloseDialoguePacket(), player);
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
    public CActionEndDialogue save(OutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CActionEndDialogue load(InputStream fileInputStream) throws IOException
    {
        return this;
    }
}

package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.tools.component.CBoolean;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CActionBranch extends CAction
{
    CBoolean clear = new CBoolean();
    CDialogueBranch targetBranch = new CDialogueBranch();

    public CActionBranch set(CDialogueBranch targetBranch)
    {
        return set(false, targetBranch);
    }

    public CActionBranch set(boolean clear, CDialogueBranch targetBranch)
    {
        this.clear.set(clear);
        this.targetBranch = targetBranch;
        return this;
    }

    @Override
    public void execute(EntityPlayerMP player)
    {
        Network.WRAPPER.sendTo(new DialogueBranchPacket(clear.value, targetBranch), player);
    }

    @Override
    public CActionBranch write(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionBranch read(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionBranch save(OutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CActionBranch load(InputStream fileInputStream) throws IOException
    {
        return this;
    }
}

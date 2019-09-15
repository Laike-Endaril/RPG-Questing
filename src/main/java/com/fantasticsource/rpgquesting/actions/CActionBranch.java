package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.tools.component.CBoolean;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
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
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        Network.WRAPPER.sendTo(new DialogueBranchPacket(clear.value, targetBranch), (EntityPlayerMP) entity);
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
    public CActionBranch save(OutputStream stream) throws IOException
    {
        clear.save(stream);
        targetBranch.save(stream);
        return this;
    }

    @Override
    public CActionBranch load(InputStream stream) throws IOException
    {
        clear.load(stream);
        targetBranch.load(stream);
        return this;
    }
}

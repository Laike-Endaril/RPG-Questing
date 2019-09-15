package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CUUID;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CActionBranch extends CAction
{
    CBoolean clear = new CBoolean();
    CUUID dialogueID = new CUUID();
    CInt branchIndex = new CInt();

    public CActionBranch set(CDialogueBranch targetBranch)
    {
        return set(false, targetBranch);
    }

    public CActionBranch set(boolean clear, CDialogueBranch targetBranch)
    {
        return set(clear, targetBranch.parent, targetBranch.parent.branches.indexOf(targetBranch));
    }

    public CActionBranch set(CDialogue dialogue, int branchIndex)
    {
        return set(false, dialogue, branchIndex);
    }

    public CActionBranch set(boolean clear, CDialogue dialogue, int branchIndex)
    {
        this.clear.set(clear);
        this.dialogueID = dialogue.permanentID;
        this.branchIndex.set(branchIndex);

        return this;
    }

    @Override
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        CDialogue dialogue = CDialogues.getByPermanentID(dialogueID.value);
        Network.WRAPPER.sendTo(new DialogueBranchPacket(clear.value, dialogue.branches.get(branchIndex.value)), (EntityPlayerMP) entity);
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
        dialogueID.save(stream);
        branchIndex.save(stream);

        return this;
    }

    @Override
    public CActionBranch load(InputStream stream) throws IOException
    {
        clear.load(stream);
        dialogueID.load(stream);
        branchIndex.load(stream);

        return this;
    }
}

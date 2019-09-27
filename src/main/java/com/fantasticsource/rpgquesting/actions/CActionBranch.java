package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.InputStream;
import java.io.OutputStream;

public class CActionBranch extends CAction
{
    CBoolean clear = new CBoolean();
    CUUID dialogueID = new CUUID();
    CInt branchIndex = new CInt();


    public CActionBranch()
    {
    }

    public CActionBranch(CDialogueBranch targetBranch)
    {
        set(targetBranch);
    }


    public CActionBranch set(CDialogueBranch targetBranch)
    {
        return set(false, targetBranch);
    }

    public CActionBranch set(boolean clear, CDialogueBranch targetBranch)
    {
        return set(clear, targetBranch.dialogue, targetBranch.dialogue.branches.indexOf(targetBranch));
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
    public String description()
    {
        return "Goes to branch " + branchIndex.value + " of dialogue: " + CDialogues.getByPermanentID(dialogueID.value).name.value;
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
    public CActionBranch save(OutputStream stream)
    {
        clear.save(stream);
        dialogueID.save(stream);
        branchIndex.save(stream);

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CActionBranch load(InputStream stream)
    {
        clear.load(stream);
        dialogueID.load(stream);
        branchIndex.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }
}

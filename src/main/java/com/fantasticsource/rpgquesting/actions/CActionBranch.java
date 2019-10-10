package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CActionBranch extends CAction
{
    private CStringUTF8 dialogueName = new CStringUTF8();
    private CInt branchIndex = new CInt();


    public CActionBranch()
    {
    }

    public CActionBranch(CDialogueBranch targetBranch)
    {
        dialogueName.set(targetBranch.dialogueName.value);
        branchIndex.set(CDialogues.get(dialogueName.value).branches.indexOf(targetBranch));
    }


    @Override
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        CDialogue dialogue = CDialogues.get(dialogueName.value);
        Network.branch((EntityPlayerMP) entity, false, dialogue.branches.get(branchIndex.value));
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Go to branch " + branchIndex.value + " of dialogue: " + dialogueName.value);
        return result;
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
        dialogueName.save(stream);
        branchIndex.save(stream);

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CActionBranch load(InputStream stream)
    {
        dialogueName.load(stream);
        branchIndex.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }
}

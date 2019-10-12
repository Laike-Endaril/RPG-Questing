package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Pair;
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


        EntityPlayerMP player = (EntityPlayerMP) entity;
        CDialogueBranch branch = CDialogues.get(dialogueName.value).branches.get(branchIndex.value);
        CDialogues.CURRENT_PLAYER_BRANCHES.put(player, new Pair<>(CDialogues.CURRENT_PLAYER_BRANCHES.get(player).getKey(), branch));
        Network.WRAPPER.sendTo(new Network.DialogueBranchPacket(false, branch), player);
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
        dialogueName.write(buf);
        branchIndex.write(buf);

        new CInt().set(conditions.size()).write(buf);
        for (CCondition condition : conditions) Component.writeMarked(buf, condition);

        return this;
    }

    @Override
    public CActionBranch read(ByteBuf buf)
    {
        dialogueName.read(buf);
        branchIndex.read(buf);

        conditions.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--) conditions.add((CCondition) Component.readMarked(buf));

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

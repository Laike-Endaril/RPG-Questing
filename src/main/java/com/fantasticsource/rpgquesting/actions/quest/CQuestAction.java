package com.fantasticsource.rpgquesting.actions.quest;

import com.fantasticsource.rpgquesting.actions.CAction;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CQuestAction extends CAction
{
    public CStringUTF8 name = new CStringUTF8();
    public CUUID dialogueID = new CUUID();
    public CInt branchIndex = new CInt();


    public CQuestAction()
    {
    }

    public CQuestAction(CQuest quest, CDialogueBranch branch)
    {
        name.set(quest.name.value);
        dialogueID.set(branch.dialogue.permanentID.value);
        branchIndex.set(branch.dialogue.branches.indexOf(branch));

        quest.relatedDialogues.add((CUUID) dialogueID.copy());
    }


    @Override
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        CQuests.complete((EntityPlayerMP) entity, name.value);
    }

    @Override
    public CQuestAction write(ByteBuf buf)
    {
        name.write(buf);
        dialogueID.write(buf);
        branchIndex.write(buf);

        buf.writeInt(conditions.size());
        for (CCondition condition : conditions) Component.writeMarked(buf, condition);

        return this;
    }

    @Override
    public CQuestAction read(ByteBuf buf)
    {
        name.read(buf);
        dialogueID.read(buf);
        branchIndex.read(buf);

        conditions.clear();
        for (int i = buf.readInt(); i > 0; i--) conditions.add((CCondition) Component.readMarked(buf));

        return this;
    }

    @Override
    public CQuestAction save(OutputStream stream)
    {
        name.save(stream);
        dialogueID.save(stream);
        branchIndex.save(stream);

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CQuestAction load(InputStream stream)
    {
        name.load(stream);
        dialogueID.load(stream);
        branchIndex.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }
}
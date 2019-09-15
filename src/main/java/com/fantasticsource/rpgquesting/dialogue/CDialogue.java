package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import scala.actors.threadpool.Arrays;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class CDialogue extends Component
{
    public CUUID permanentID = new CUUID().set(UUID.randomUUID());
    public CStringUTF8 name = new CStringUTF8();

    public ArrayList<CCondition> dialogueConditions = new ArrayList<>();
    public ArrayList<CDialogueBranch> branches = new ArrayList<>();

    public CUUID sessionID = new CUUID().set(UUID.randomUUID());

    public CDialogue setName(String name)
    {
        this.name.set(name);
        return this;
    }

    public CDialogue add(CCondition... conditions)
    {
        dialogueConditions.addAll(Arrays.asList(conditions));
        return this;
    }

    public CDialogue add(CDialogueBranch... branches)
    {
        for (CDialogueBranch branch : branches)
        {
            this.branches.add(branch.setParent(this));
        }
        return this;
    }

    public boolean entityHas(Entity entity)
    {
        for (CCondition condition : dialogueConditions)
        {
            if (condition.unmetConditions(entity).size() > 0) return false;
        }
        return true;
    }

    @Override
    public CDialogue write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogue read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogue save(OutputStream stream) throws IOException
    {
        permanentID.save(stream);
        name.save(stream);

        new CInt().set(dialogueConditions.size()).save(stream);
        for (CCondition condition : dialogueConditions) Component.saveMarked(stream, condition);

        new CInt().set(branches.size()).save(stream);
        for (CDialogueBranch branch : branches) branch.save(stream);

        return this;
    }

    @Override
    public CDialogue load(InputStream stream) throws IOException
    {
        permanentID.load(stream);
        name.load(stream);

        dialogueConditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) dialogueConditions.add((CCondition) Component.loadMarked(stream));

        branches.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) branches.add(new CDialogueBranch().load(stream));

        return this;
    }
}

package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import scala.actors.threadpool.Arrays;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class CDialogue extends Component
{
    public CUUID permanentID = new CUUID().set(UUID.randomUUID());
    public CStringUTF8 name = new CStringUTF8(), group = new CStringUTF8();

    public ArrayList<CCondition> playerConditions = new ArrayList<>();
    public ArrayList<CCondition> entityConditions = new ArrayList<>();
    public ArrayList<CDialogueBranch> branches = new ArrayList<>();

    public CDialogue()
    {
    }

    public CDialogue(String name, String group)
    {
        this.name.set(name);
        this.group.set(group);
    }

    public CDialogue addPlayerConditions(CCondition... conditions)
    {
        playerConditions.addAll(Arrays.asList(conditions));
        return this;
    }

    public CDialogue addEntityConditions(CCondition... conditions)
    {
        entityConditions.addAll(Arrays.asList(conditions));
        return this;
    }

    public CDialogue add(CDialogueBranch... branches)
    {
        for (CDialogueBranch branch : branches)
        {
            this.branches.add(branch.setDialogue(this));
        }
        return this;
    }

    public boolean isAvailable(EntityPlayerMP player, Entity entity)
    {
        for (CCondition condition : playerConditions)
        {
            if (condition.unmetConditions(player).size() > 0) return false;
        }
        for (CCondition condition : entityConditions)
        {
            if (condition.unmetConditions(entity).size() > 0) return false;
        }
        return true;
    }

    @Override
    public CDialogue write(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CDialogue read(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CDialogue save(OutputStream stream)
    {
        permanentID.save(stream);
        name.save(stream);
        group.save(stream);

        new CInt().set(playerConditions.size()).save(stream);
        for (CCondition condition : playerConditions) Component.saveMarked(stream, condition);

        new CInt().set(entityConditions.size()).save(stream);
        for (CCondition condition : entityConditions) Component.saveMarked(stream, condition);

        new CInt().set(branches.size()).save(stream);
        for (CDialogueBranch branch : branches) branch.save(stream);

        return this;
    }

    @Override
    public CDialogue load(InputStream stream)
    {
        permanentID.load(stream);
        CDialogues.add(this);
        name.load(stream);
        group.load(stream);

        playerConditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) playerConditions.add((CCondition) Component.loadMarked(stream));

        entityConditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) entityConditions.add((CCondition) Component.loadMarked(stream));

        branches.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) branches.add(new CDialogueBranch());
        for (CDialogueBranch branch : branches) branch.load(stream);

        return this;
    }
}

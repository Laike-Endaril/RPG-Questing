package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.component.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static com.fantasticsource.rpgquesting.quest.CQuests.QUESTS;

public class CQuest extends Component implements IObfuscatedComponent
{
    public CUUID permanentID = new CUUID().set(UUID.randomUUID());
    public CStringUTF8 name = new CStringUTF8();
    public CStringUTF8 group = new CStringUTF8();
    public CInt level = new CInt();
    public CBoolean repeatable = new CBoolean();
    public CInt experience = new CInt();
    public ArrayList<CRelatedDialogueEntry> relatedDialogues = new ArrayList<>();

    public ArrayList<CObjective> objectives = new ArrayList<>();

    public ArrayList<CItemStack> rewards = new ArrayList<>();

    public ArrayList<CCondition> conditions = new ArrayList<>();

    private boolean recursion = false;
    private long lastWarning = -1;


    public CQuest()
    {
    }

    public CQuest(String name, String group, int level, boolean repeatable)
    {
        this.name.set(name);
        this.group.set(group);
        this.level.set(level);
        this.repeatable.set(repeatable);
    }


    public CQuest addConditions(CCondition... conditions)
    {
        this.conditions.addAll(Arrays.asList(conditions));
        return this;
    }

    public CQuest addObjectives(CObjective... objectives)
    {
        this.objectives.addAll(Arrays.asList(objectives));
        return this;
    }

    public CQuest addRewards(ItemStack... rewards)
    {
        for (ItemStack stack : rewards) this.rewards.add(new CItemStack(stack));
        return this;
    }

    public CQuest setExp(int exp)
    {
        experience.set(exp);
        return this;
    }

    public final boolean isAvailable(EntityPlayerMP player)
    {
        if (QUESTS.worldQuestData.get(name.value) != this) return false;

        if (isInProgress(player)) return false;

        if (isReadyToComplete(player)) return false;

        if (!repeatable.value && isCompleted(player)) return false;

        if (recursion)
        {
            if ((lastWarning == -1 || ServerTickTimer.currentTick() - lastWarning > 1200))
            {
                System.err.println("ERROR: infinite recursion detected in quest availability conditions for quest: " + name.value);
                System.err.println("This happens when you require a quest be available in its own availability conditions (which is redundant), or have an indirect loop of 2 or more quests requiring each other be available");
                System.err.println("Edit quest availability conditions to remove this error message");
                lastWarning = ServerTickTimer.currentTick();
            }
            return false;
        }


        recursion = true;
        for (CCondition condition : conditions)
        {
            if (condition.unmetConditions(player).size() > 0)
            {
                recursion = false;
                return false;
            }
        }
        recursion = false;

        return true;
    }

    public final boolean isInProgress(EntityPlayerMP player)
    {
        return CQuests.isInProgress(player, this);
    }

    public final boolean isReadyToComplete(EntityPlayerMP player)
    {
        return CQuests.isReadyToComplete(player, this);
    }

    public final boolean isCompleted(EntityPlayerMP player)
    {
        return CQuests.isCompleted(player, this);
    }

    @Override
    public CQuest write(ByteBuf buf)
    {
        name.write(buf);
        group.write(buf);
        level.write(buf);
        repeatable.write(buf);

        buf.writeInt(conditions.size());
        for (CCondition condition : conditions) Component.writeMarked(buf, condition);

        buf.writeInt(objectives.size());
        for (CObjective objective : objectives) Component.writeMarked(buf, objective);

        experience.write(buf);
        buf.writeInt(rewards.size());
        for (CItemStack reward : rewards) reward.write(buf);

        buf.writeInt(relatedDialogues.size());
        for (CRelatedDialogueEntry dialogueEntry : relatedDialogues) dialogueEntry.write(buf);

        return this;
    }

    @Override
    public CQuest read(ByteBuf buf)
    {
        name.read(buf);
        group.read(buf);
        level.read(buf);
        repeatable.read(buf);

        conditions.clear();
        for (int i = buf.readInt(); i > 0; i--) conditions.add((CCondition) Component.readMarked(buf));

        objectives.clear();
        for (int i = buf.readInt(); i > 0; i--) objectives.add((CObjective) Component.readMarked(buf));

        experience.read(buf);
        rewards.clear();
        for (int i = buf.readInt(); i > 0; i--) rewards.add(new CItemStack().read(buf));

        relatedDialogues.clear();
        for (int i = buf.readInt(); i > 0; i--) relatedDialogues.add(new CRelatedDialogueEntry().read(buf));

        return this;
    }

    @Override
    public CQuest save(OutputStream stream)
    {
        permanentID.save(stream);
        name.save(stream);
        group.save(stream);
        level.save(stream);
        repeatable.save(stream);

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        new CInt().set(objectives.size()).save(stream);
        for (CObjective objective : objectives) Component.saveMarked(stream, objective);

        experience.save(stream);
        new CInt().set(rewards.size()).save(stream);
        for (CItemStack reward : rewards) reward.save(stream);

        new CInt().set(relatedDialogues.size()).save(stream);
        for (CRelatedDialogueEntry dialogueEntry : relatedDialogues) dialogueEntry.save(stream);

        return this;
    }

    @Override
    public CQuest load(InputStream stream)
    {
        permanentID.load(stream);
        name.load(stream);
        group.load(stream);
        level.load(stream);
        repeatable.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        objectives.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) objectives.add((CObjective) Component.loadMarked(stream));

        experience.load(stream);
        rewards.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) rewards.add(new CItemStack().load(stream));

        relatedDialogues.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) relatedDialogues.add(new CRelatedDialogueEntry().load(stream));

        return this;
    }

    @Override
    public CQuest writeObf(ByteBuf buf)
    {
        name.write(buf);
        group.write(buf);
        level.write(buf);
        repeatable.write(buf);

        new CInt().set(objectives.size()).write(buf);
        for (CObjective objective : objectives) IObfuscatedComponent.writeMarkedObf(buf, objective);

        return this;
    }

    @Override
    public CQuest readObf(ByteBuf buf)
    {
        name.read(buf);
        group.read(buf);
        level.read(buf);
        repeatable.read(buf);

        objectives.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--) objectives.add((CObjective) IObfuscatedComponent.readMarkedObf(buf));

        return this;
    }
}

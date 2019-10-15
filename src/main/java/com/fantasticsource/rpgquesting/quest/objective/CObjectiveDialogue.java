package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUIObjective;
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.datastructures.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CObjectiveDialogue extends CObjective
{
    public CBoolean done = new CBoolean();
    public CStringUTF8 dialogueName = new CStringUTF8();
    public CInt branchIndex = new CInt();

    public CObjectiveDialogue()
    {
    }

    public CObjectiveDialogue(String text, String dialogueName, int branchIndex)
    {
        this.text.set(text);
        this.dialogueName.set(dialogueName);
        this.branchIndex.set(branchIndex);
    }

    public static void onDialogue(EntityPlayerMP player, String dialogueName, int branchIndex)
    {
        CPlayerQuestData data = CQuests.playerQuestData.get(player.getPersistentID());
        if (data == null) return;

        boolean changed = false;
        for (LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> map : data.inProgressQuests.values())
        {
            for (Pair<CUUID, ArrayList<CObjective>> value : map.values())
            {
                ArrayList<CObjective> objectives = value.getValue();

                for (CObjective objective : objectives)
                {
                    if (objective.getClass() == CObjectiveDialogue.class)
                    {
                        CObjectiveDialogue objectiveDialogue = (CObjectiveDialogue) objective;
                        if (!objectiveDialogue.done.value && dialogueName.equals(objectiveDialogue.dialogueName.value) && branchIndex == objectiveDialogue.branchIndex.value)
                        {
                            objectiveDialogue.done.set(true);
                            changed = true;
                        }
                    }
                }
            }
        }

        if (changed) data.saveAndSync();
    }

    @Override
    protected String progressText()
    {
        return done.value ? "[x]" : "[ ]";
    }

    @Override
    public boolean isStarted()
    {
        return isDone();
    }

    @Override
    public boolean isDone()
    {
        return done.value;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIObjective getChoosableElement(GUIScreen screen)
    {
        return new GUIObjective(screen, new CObjectiveDialogue("Talk to NPC", "Dialogue Name", 0));
    }

    @Override
    public CObjectiveDialogue write(ByteBuf buf)
    {
        super.write(buf);

        done.write(buf);
        dialogueName.write(buf);
        branchIndex.write(buf);

        return this;
    }

    @Override
    public CObjectiveDialogue read(ByteBuf buf)
    {
        super.read(buf);

        done.read(buf);
        dialogueName.read(buf);
        branchIndex.read(buf);

        return this;
    }

    @Override
    public CObjectiveDialogue save(OutputStream stream)
    {
        super.save(stream);

        done.save(stream);
        dialogueName.save(stream);
        branchIndex.save(stream);

        return this;
    }

    @Override
    public CObjectiveDialogue load(InputStream stream)
    {
        super.load(stream);

        done.load(stream);
        dialogueName.load(stream);
        branchIndex.load(stream);

        return this;
    }

    @Override
    public CObjectiveDialogue writeObf(ByteBuf buf)
    {
        super.writeObf(buf);

        done.write(buf);

        return this;
    }

    @Override
    public CObjectiveDialogue readObf(ByteBuf buf)
    {
        super.readObf(buf);

        done.read(buf);

        return this;
    }
}

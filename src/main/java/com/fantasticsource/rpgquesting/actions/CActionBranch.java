package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.gui.GUIAction;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.datastructures.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CActionBranch extends CAction
{
    public static String queuedDialogueName = "X";
    public CStringUTF8 dialogueName = new CStringUTF8().set(queuedDialogueName);
    public CInt branchIndex = new CInt();


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
        Network.branch(player, false, branch);
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
        super.write(buf);

        dialogueName.write(buf);
        branchIndex.write(buf);

        return this;
    }

    @Override
    public CActionBranch read(ByteBuf buf)
    {
        super.read(buf);

        dialogueName.read(buf);
        branchIndex.read(buf);

        return this;
    }

    @Override
    public CActionBranch save(OutputStream stream)
    {
        super.save(stream);

        dialogueName.save(stream);
        branchIndex.save(stream);

        return this;
    }

    @Override
    public CActionBranch load(InputStream stream)
    {
        super.load(stream);

        dialogueName.load(stream);
        branchIndex.load(stream);

        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIAction getChoosableElement(GUIScreen screen)
    {
        return new GUIAction(screen, new CActionBranch());
    }
}

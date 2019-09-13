package com.fantasticsource.rpgquesting.dialogue.actions;

import com.fantasticsource.mctools.component.CBoolean;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CActionBranch extends CAction
{
    CBoolean clear = new CBoolean();
    CDialogueBranch targetBranch = new CDialogueBranch();

    @Override
    public void execute(EntityPlayerMP player)
    {
        Network.WRAPPER.sendTo(new DialogueBranchPacket(clear.value, targetBranch), player);
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
    public CActionBranch save(FileOutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CActionBranch load(FileInputStream fileInputStream) throws IOException
    {
        return this;
    }

    @Override
    public CActionBranch parse(String s)
    {
        return this;
    }

    @Override
    public CActionBranch copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen guiScreen)
    {
        return null;
    }

    @Override
    public CActionBranch setFromGUIElement(GUIElement guiElement)
    {
        return this;
    }
}

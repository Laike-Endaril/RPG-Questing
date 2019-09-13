package com.fantasticsource.rpgquesting.dialogue.actions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.CloseDialoguePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CActionEndDialogue extends CAction
{
    @Override
    public void execute(EntityPlayerMP player)
    {
        Network.WRAPPER.sendTo(new CloseDialoguePacket(), player);
    }

    @Override
    public CActionEndDialogue write(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionEndDialogue read(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionEndDialogue save(FileOutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CActionEndDialogue load(FileInputStream fileInputStream) throws IOException
    {
        return this;
    }

    @Override
    public CActionEndDialogue parse(String s)
    {
        return this;
    }

    @Override
    public CActionEndDialogue copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen guiScreen)
    {
        return null;
    }

    @Override
    public CActionEndDialogue setFromGUIElement(GUIElement guiElement)
    {
        return this;
    }
}

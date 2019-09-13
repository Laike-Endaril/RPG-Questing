package com.fantasticsource.rpgquesting.dialogue.actions;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CAction extends Component
{
    public void execute(EntityPlayerMP player)
    {
    }

    @Override
    public CAction write(ByteBuf buf)
    {
        new CStringUTF8().set(getClass().getName()).write(buf);
        return this;
    }

    @Override
    public CAction read(ByteBuf buf)
    {
        try
        {
            return ((CAction) Class.forName(new CStringUTF8().read(buf).value).newInstance()).read(buf);
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public CAction save(FileOutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CAction load(FileInputStream fileInputStream) throws IOException
    {
        return this;
    }

    @Override
    public CAction parse(String s)
    {
        return this;
    }

    @Override
    public CAction copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen guiScreen)
    {
        return null;
    }

    @Override
    public CAction setFromGUIElement(GUIElement guiElement)
    {
        return this;
    }
}

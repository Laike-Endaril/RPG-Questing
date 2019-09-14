package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.component.IObfuscatedComponent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.rpgquesting.dialogue.actions.CAction;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import scala.actors.threadpool.Arrays;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CDialogueChoice extends Component implements IObfuscatedComponent
{
    public CStringUTF8 text = new CStringUTF8();
    public ArrayList<CAction> actions = new ArrayList<>();

    public void execute(EntityPlayerMP player)
    {
        for (CAction action : actions) action.execute(player);
    }

    public CDialogueChoice setText(String text)
    {
        this.text.set(text);
        return this;
    }

    public CDialogueChoice add(CAction... actions)
    {
        this.actions.addAll(Arrays.asList(actions));
        return this;
    }

    @Override
    public CDialogueChoice write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogueChoice read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogueChoice save(FileOutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogueChoice load(FileInputStream fileInputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogueChoice parse(String s)
    {
        return this;
    }

    @Override
    public CDialogueChoice copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen guiScreen)
    {
        return null;
    }

    @Override
    public CDialogueChoice setFromGUIElement(GUIElement guiElement)
    {
        return this;
    }

    @Override
    public CDialogueChoice writeObf(ByteBuf buf)
    {
        text.write(buf);
        return this;
    }

    @Override
    public CDialogueChoice readObf(ByteBuf buf)
    {
        text.read(buf);
        return this;
    }
}

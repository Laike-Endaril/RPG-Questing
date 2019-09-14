package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.rpgquesting.actions.CAction;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.IObfuscatedComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CDialogueChoice extends Component implements IObfuscatedComponent
{
    public CStringUTF8 text = new CStringUTF8();
    public CAction action;

    public void execute(EntityPlayerMP player)
    {
        action.tryExecute(player);
    }

    public CDialogueChoice setText(String text)
    {
        this.text.set(text);
        return this;
    }

    public CDialogueChoice setAction(CAction action)
    {
        this.action = action;
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
    public CDialogueChoice save(OutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogueChoice load(InputStream fileInputStream) throws IOException
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

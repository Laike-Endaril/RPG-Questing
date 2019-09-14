package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
            return null;
        }
    }

    @Override
    public CAction save(OutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CAction load(InputStream fileInputStream) throws IOException
    {
        return this;
    }
}

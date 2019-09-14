package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CObjective extends Component
{
    CStringUTF8 text = new CStringUTF8();

    @Override
    public CObjective write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CObjective read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CObjective save(OutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CObjective load(InputStream fileInputStream) throws IOException
    {
        return this;
    }
}

package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CQuest extends Component
{
    public CStringUTF8 saveName = new CStringUTF8();
    public CInt level = new CInt();

    @Override
    public CQuest write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CQuest read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CQuest save(OutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CQuest load(InputStream fileInputStream) throws IOException
    {
        return this;
    }
}

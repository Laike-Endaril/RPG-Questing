package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Quest extends Component
{
    public CStringUTF8 name, description;

    @Override
    public void write(ByteBuf byteBuf)
    {

    }

    @Override
    public void read(ByteBuf byteBuf)
    {

    }

    @Override
    public void save(FileOutputStream fileOutputStream) throws IOException
    {

    }

    @Override
    public void load(FileInputStream fileInputStream) throws IOException
    {

    }

    @Override
    public void parse(String s)
    {

    }

    @Override
    public Component copy()
    {
        return null;
    }
}

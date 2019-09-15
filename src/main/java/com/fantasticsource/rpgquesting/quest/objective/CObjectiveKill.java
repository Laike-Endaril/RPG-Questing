package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class CObjectiveKill extends CObjective
{
    CInt current = new CInt(), required = new CInt();

    @Override
    protected String progressText()
    {
        if (required.value > 1) return current.value + "/" + required.value;
        else if (current.value == 1) return "[x]";
        else return "[ ]";
    }

    @Override
    public CObjectiveKill write(ByteBuf buf)
    {
        owner.write(buf);
        text.write(buf);
        current.write(buf);
        required.write(buf);
        return this;
    }

    @Override
    public CObjectiveKill read(ByteBuf buf)
    {
        owner.read(buf);
        text.read(buf);
        current.read(buf);
        required.read(buf);
        return this;
    }

    @Override
    public CObjectiveKill save(OutputStream stream) throws IOException
    {
        owner.save(stream);
        text.save(stream);
        current.save(stream);
        required.save(stream);
        return this;
    }

    @Override
    public CObjectiveKill load(InputStream stream) throws IOException
    {
        owner.load(stream);
        text.load(stream);
        current.load(stream);
        required.load(stream);
        return this;
    }
}

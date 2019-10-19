package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CObjectiveArea extends CStateBasedObjective
{
    public final CInt[] coords = new CInt[6];

    public CObjectiveArea()
    {
        coords[0] = new CInt();
        coords[1] = new CInt();
        coords[2] = new CInt();
        coords[3] = new CInt();
        coords[4] = new CInt();
        coords[5] = new CInt();
    }

    public CObjectiveArea(String text, int x1, int y1, int z1, int x2, int y2, int z2)
    {
        this.text.set(text);

        int swap;
        if (x1 > x2)
        {
            swap = x1;
            x1 = x2;
            x2 = swap;
        }
        if (y1 > y2)
        {
            swap = y1;
            y1 = y2;
            y2 = swap;
        }
        if (z1 > z2)
        {
            swap = z1;
            z1 = z2;
            z2 = swap;
        }

        coords[0] = new CInt().set(x1);
        coords[1] = new CInt().set(y1);
        coords[2] = new CInt().set(z1);
        coords[3] = new CInt().set(x2);
        coords[4] = new CInt().set(y2);
        coords[5] = new CInt().set(z2);
    }

    @Override
    public CObjectiveArea write(ByteBuf buf)
    {
        super.write(buf);

        for (CInt cInt : coords) cInt.write(buf);

        return this;
    }

    @Override
    public CObjectiveArea read(ByteBuf buf)
    {
        super.read(buf);

        for (CInt cInt : coords) cInt.read(buf);

        return this;
    }

    @Override
    public CObjectiveArea save(OutputStream stream)
    {
        super.save(stream);

        for (CInt cInt : coords) cInt.save(stream);

        return this;
    }

    @Override
    public CObjectiveArea load(InputStream stream)
    {
        super.load(stream);

        for (CInt cInt : coords) cInt.load(stream);

        return this;
    }
}

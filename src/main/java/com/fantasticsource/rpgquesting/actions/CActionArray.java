package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CActionArray extends CAction
{
    public ArrayList<CAction> actions = new ArrayList<>();

    @Override
    protected void execute(EntityPlayerMP player)
    {
        for (CAction action : actions) action.execute(player);
    }

    @Override
    public CActionArray write(ByteBuf buf)
    {
        buf.writeInt(actions.size());
        for (CAction action : actions) Component.writeMarked(buf, action);
        return this;
    }

    @Override
    public CActionArray read(ByteBuf buf)
    {
        for (int i = buf.readInt(); i > 0; i--) actions.add((CAction) Component.readMarked(buf));
        return this;
    }

    @Override
    public CActionArray save(OutputStream stream) throws IOException
    {
        new CInt().set(actions.size()).save(stream);
        for (CAction action : actions) Component.saveMarked(stream, action);
        return this;
    }

    @Override
    public CActionArray load(InputStream stream) throws IOException
    {
        for (int i = new CInt().load(stream).value; i > 0; i--) actions.add((CAction) Component.loadMarked(stream));
        return this;
    }
}

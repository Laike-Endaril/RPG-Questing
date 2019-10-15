package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUIObjective;
import com.fantasticsource.tools.component.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CObjective extends Component implements IObfuscatedComponent
{
    public CStringUTF8 text = new CStringUTF8();
    public CBoolean progressIsPrefix = new CBoolean().set(true);
    CUUID owner = new CUUID();

    public final CObjective setOwner(EntityPlayerMP player)
    {
        owner.set(player.getPersistentID());
        return this;
    }

    public final String getFullText()
    {
        if (progressIsPrefix.value) return progressText() + " " + text.value;

        return text.value + " " + progressText();
    }

    protected abstract String progressText();

    public abstract boolean isStarted();

    public abstract boolean isDone();

    @SideOnly(Side.CLIENT)
    public abstract GUIObjective getChoosableElement(GUIScreen screen);

    @Override
    public CObjective write(ByteBuf buf)
    {
        new CBoolean().set(owner.value != null).write(buf);
        if (owner.value != null) owner.write(buf);

        text.write(buf);
        progressIsPrefix.write(buf);

        return this;
    }

    @Override
    public CObjective read(ByteBuf buf)
    {
        if (new CBoolean().read(buf).value) owner.read(buf);

        text.read(buf);
        progressIsPrefix.read(buf);

        return this;
    }

    @Override
    public CObjective save(OutputStream stream)
    {
        new CBoolean().set(owner.value != null).save(stream);
        if (owner.value != null) owner.save(stream);

        text.save(stream);
        progressIsPrefix.save(stream);

        return this;
    }

    @Override
    public CObjective load(InputStream stream)
    {
        if (new CBoolean().load(stream).value) owner.load(stream);

        text.load(stream);
        progressIsPrefix.load(stream);

        return this;
    }

    @Override
    public CObjective writeObf(ByteBuf buf)
    {
        text.write(buf);
        progressIsPrefix.write(buf);

        return this;
    }

    @Override
    public CObjective readObf(ByteBuf buf)
    {
        text.read(buf);
        progressIsPrefix.read(buf);

        return this;
    }
}

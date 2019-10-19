package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUIObjective;
import com.fantasticsource.tools.component.CBoolean;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;

public class CObjectiveEnterArea extends CObjectiveArea
{
    public final CBoolean done = new CBoolean();

    public CObjectiveEnterArea()
    {
        super();
    }

    public CObjectiveEnterArea(String text, int x1, int y1, int z1, int x2, int y2, int z2)
    {
        super(text, x1, y1, z1, x2, y2, z2);
    }

    @Override
    public boolean check(EntityPlayerMP player)
    {
        BlockPos pos = player.getPosition();

        if (!done.value
                && pos.getX() >= coords[0].value && pos.getX() <= coords[3].value
                && pos.getY() >= coords[1].value && pos.getY() <= coords[4].value
                && pos.getZ() >= coords[2].value && pos.getZ() <= coords[5].value)
        {
            done.set(true);
            return true;
        }

        return false;
    }

    @Override
    protected String progressText()
    {
        return done.value ? "[x]" : "[ ]";
    }

    @Override
    public boolean isStarted()
    {
        return isDone();
    }

    @Override
    public boolean isDone()
    {
        return done.value;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIObjective getChoosableElement(GUIScreen screen)
    {
        return new GUIObjective(screen, new CObjectiveEnterArea("Visit area", 0, 0, 0, 1, 1, 1));
    }

    @Override
    public CObjectiveEnterArea write(ByteBuf buf)
    {
        super.write(buf);

        done.write(buf);

        return this;
    }

    @Override
    public CObjectiveEnterArea read(ByteBuf buf)
    {
        super.read(buf);

        done.read(buf);

        return this;
    }

    @Override
    public CObjectiveEnterArea save(OutputStream stream)
    {
        super.save(stream);

        done.save(stream);

        return this;
    }

    @Override
    public CObjectiveEnterArea load(InputStream stream)
    {
        super.load(stream);

        done.load(stream);

        return this;
    }

    @Override
    public CObjectiveEnterArea writeObf(ByteBuf buf)
    {
        super.writeObf(buf);

        done.write(buf);

        return this;
    }

    @Override
    public CObjectiveEnterArea readObf(ByteBuf buf)
    {
        super.readObf(buf);

        done.read(buf);

        return this;
    }
}

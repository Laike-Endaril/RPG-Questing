package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUIAction;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CActionCommand extends CAction
{
    public CStringUTF8 command = new CStringUTF8();

    public CActionCommand()
    {
    }

    public CActionCommand(String command)
    {
        set(command);
    }


    public CActionCommand set(String command)
    {
        this.command.set(command);
        return this;
    }


    @Override
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        server.commandManager.executeCommand(server, command.value.replace("@p", entity.getName()));
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Run command: " + command.value);
        return result;
    }

    @Override
    public CActionCommand write(ByteBuf buf)
    {
        super.write(buf);

        command.write(buf);

        return this;
    }

    @Override
    public CActionCommand read(ByteBuf buf)
    {
        super.read(buf);

        command.read(buf);

        return this;
    }

    @Override
    public CActionCommand save(OutputStream stream)
    {
        super.save(stream);

        command.save(stream);

        return this;
    }

    @Override
    public CActionCommand load(InputStream stream)
    {
        super.load(stream);

        command.load(stream);

        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIAction getChoosableElement(GUIScreen screen)
    {
        return new GUIAction(screen, new CActionCommand("command"));
    }
}

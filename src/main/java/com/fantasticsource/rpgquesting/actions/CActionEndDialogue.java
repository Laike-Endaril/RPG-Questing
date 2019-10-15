package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.CloseDialoguePacket;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.gui.GUIAction;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CActionEndDialogue extends CAction
{
    @Override
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;
        Network.WRAPPER.sendTo(new CloseDialoguePacket(), (EntityPlayerMP) entity);
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Close the current dialogue");
        return result;
    }

    @Override
    public CActionEndDialogue write(ByteBuf buf)
    {
        new CInt().set(conditions.size()).write(buf);
        for (CCondition condition : conditions) Component.writeMarked(buf, condition);

        return this;
    }

    @Override
    public CActionEndDialogue read(ByteBuf buf)
    {
        conditions.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--) conditions.add((CCondition) Component.readMarked(buf));

        return this;
    }

    @Override
    public CActionEndDialogue save(OutputStream stream)
    {
        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CActionEndDialogue load(InputStream stream)
    {
        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIAction getChoosableElement(GUIScreen screen)
    {
        return new GUIAction(screen, new CActionEndDialogue());
    }
}

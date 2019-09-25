package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.actions.CAction;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.IObfuscatedComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CDialogueChoice extends Component implements IObfuscatedComponent
{
    public CStringUTF8 text = new CStringUTF8();
    public ArrayList<CCondition> availabilityConditions = new ArrayList<>();
    public CAction action;


    public CDialogueChoice()
    {
    }

    public CDialogueChoice(String text)
    {
        setText(text);
    }


    public boolean isAvailable(EntityPlayerMP player)
    {
        for (CCondition condition : availabilityConditions) if (condition.unmetConditions(player).size() > 0) return false;
        return true;
    }

    public void execute(EntityPlayerMP player)
    {
        ArrayList<String> unmetConditions = action.tryExecute(player);
        if (unmetConditions.size() > 0)
        {
            Network.WRAPPER.sendTo(new Network.ActionErrorPacket(text.value, unmetConditions), player);
        }
    }

    public CDialogueChoice setText(String text)
    {
        this.text.set(text);
        return this;
    }

    public CDialogueChoice setAction(CAction action)
    {
        this.action = action;
        return this;
    }

    @Override
    public CDialogueChoice write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogueChoice read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogueChoice save(OutputStream stream)
    {
        text.save(stream);

        new CInt().set(availabilityConditions.size()).save(stream);
        for (CCondition condition : availabilityConditions) Component.saveMarked(stream, condition);

        Component.saveMarked(stream, action);
        return this;
    }

    @Override
    public CDialogueChoice load(InputStream stream)
    {
        text.load(stream);

        availabilityConditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) availabilityConditions.add((CCondition) Component.loadMarked(stream));

        action = (CAction) Component.loadMarked(stream);
        return this;
    }

    @Override
    public CDialogueChoice writeObf(ByteBuf buf)
    {
        text.write(buf);
        return this;
    }

    @Override
    public CDialogueChoice readObf(ByteBuf buf)
    {
        text.read(buf);
        return this;
    }
}

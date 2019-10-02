package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.rpgquesting.gui.GUIObjective;
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CObjectiveCollect extends CObjective
{
    static
    {
        MinecraftForge.EVENT_BUS.register(CObjectiveCollect.class);
    }

    CInt current = new CInt();
    CItemStack stackToMatch = new CItemStack();

    public CObjectiveCollect()
    {
    }

    public CObjectiveCollect(String text, ItemStack stackToMatch)
    {
        this.text.set(text);
        this.stackToMatch.set(stackToMatch.copy());
    }

    @SubscribeEvent
    public static void serverPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.END) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        CPlayerQuestData data = CQuests.playerQuestData.get(player.getPersistentID());
        if (data == null) return;


        boolean changed = false;
        for (LinkedHashMap<String, ArrayList<CObjective>> map : data.inProgressQuests.values())
        {
            for (ArrayList<CObjective> objectives : map.values())
            {
                for (CObjective objective : objectives)
                {
                    if (objective.getClass() == CObjectiveCollect.class)
                    {
                        changed |= ((CObjectiveCollect) objective).serverUpdate(player);
                    }
                }
            }
        }

        if (changed) data.saveAndSync();
    }

    private boolean serverUpdate(EntityPlayerMP player)
    {
        int result = 0;
        InventoryPlayer inv = player.inventory;
        for (int i = inv.getSizeInventory() - 1; i >= 0; i--)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (ItemMatcher.stacksMatch(stack, stackToMatch.stack)) result += stack.getCount();
        }

        if (result != current.value)
        {
            current.set(result);
            return true;
        }
        return false;
    }

    @Override
    protected String progressText()
    {
        int required = stackToMatch.stack.getCount();

        if (required > 1) return "(" + current.value + "/" + required + ")";
        else if (current.value == 1) return "[x]";
        else return "[ ]";
    }

    @Override
    public boolean isStarted()
    {
        return isDone() || current.value > 0;
    }

    @Override
    public boolean isDone()
    {
        //Server-side only; last-second update before check, to prevent exploits
        if (owner.value != null)
        {
            PlayerData data = PlayerData.get(owner.value);
            if (data != null)
            {
                EntityPlayer player = data.player;
                if (player != null) serverUpdate((EntityPlayerMP) player);
            }
        }


        //Both sides
        return current.value >= stackToMatch.stack.getCount();
    }

    @Override
    public GUIObjective getChoosableElement(GUIScreen screen)
    {
        GUIObjective guiObjective = new GUIObjective(screen, new CObjectiveCollect("Items collected", new ItemStack(Items.AIR, 1)));
        guiObjective.text = guiObjective.text.replace("[ ]", "(?/?)");
        return guiObjective;
    }

    @Override
    public CObjectiveCollect write(ByteBuf buf)
    {
        text.write(buf);
        current.write(buf);
        stackToMatch.write(buf);
        return this;
    }

    @Override
    public CObjectiveCollect read(ByteBuf buf)
    {
        text.read(buf);
        current.read(buf);
        stackToMatch.read(buf);
        return this;
    }

    @Override
    public CObjectiveCollect save(OutputStream stream)
    {
        new CBoolean().set(owner.value != null).save(stream);
        if (owner.value != null) owner.save(stream);
        text.save(stream);
        current.save(stream);
        stackToMatch.save(stream);
        return this;
    }

    @Override
    public CObjectiveCollect load(InputStream stream)
    {
        if (new CBoolean().load(stream).value) owner.load(stream);
        text.load(stream);
        current.load(stream);
        stackToMatch.load(stream);
        return this;
    }
}

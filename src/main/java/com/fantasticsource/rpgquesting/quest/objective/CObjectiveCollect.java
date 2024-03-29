package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.rpgquesting.gui.GUIObjective;
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;

public class CObjectiveCollect extends CStateBasedObjective
{
    public CItemStack stackToMatch = new CItemStack();
    protected CInt current = new CInt();

    public CObjectiveCollect()
    {
    }

    public CObjectiveCollect(String text, ItemStack stackToMatch)
    {
        this.text.set(text);
        this.stackToMatch.set(stackToMatch.copy());
    }

    @Override
    public boolean check(EntityPlayerMP player)
    {
        int result = 0;
        InventoryPlayer inv = player.inventory;
        for (int i = inv.getSizeInventory() - 1; i >= 0; i--)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (ItemMatcher.stacksMatch(stack, stackToMatch.value)) result += stack.getCount();
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
        int required = stackToMatch.value.getCount();

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
        //Server-side only; last-second update before check, to help prevent exploits
        if (owner.value != null)
        {
            PlayerData data = PlayerData.get(owner.value);
            if (data != null)
            {
                EntityPlayer player = data.player;
                if (player != null && check((EntityPlayerMP) player))
                {
                    CPlayerQuestData questData = CQuests.playerQuestData.get(player);
                    if (questData != null) questData.saveAndSync();
                }
            }
        }


        //Both sides
        return current.value >= stackToMatch.value.getCount();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIObjective getChoosableElement(GUIScreen screen)
    {
        GUIObjective guiObjective = new GUIObjective(screen, new CObjectiveCollect("Collect X items", new ItemStack(Items.AIR, 1)));
        guiObjective.text = guiObjective.text.replace("[ ]", "(0/X)");
        return guiObjective;
    }

    @Override
    public CObjectiveCollect write(ByteBuf buf)
    {
        super.write(buf);

        current.write(buf);
        stackToMatch.write(buf);

        return this;
    }

    @Override
    public CObjectiveCollect read(ByteBuf buf)
    {
        super.read(buf);

        current.read(buf);
        stackToMatch.read(buf);

        return this;
    }

    @Override
    public CObjectiveCollect save(OutputStream stream)
    {
        super.save(stream);

        current.save(stream);
        stackToMatch.save(stream);

        return this;
    }

    @Override
    public CObjectiveCollect load(InputStream stream)
    {
        super.load(stream);

        current.load(stream);
        stackToMatch.load(stream);

        return this;
    }

    @Override
    public CObjectiveCollect writeObf(ByteBuf buf)
    {
        super.writeObf(buf);

        current.write(buf);
        stackToMatch.write(buf);

        return this;
    }

    @Override
    public CObjectiveCollect readObf(ByteBuf buf)
    {
        super.readObf(buf);

        current.read(buf);
        stackToMatch.read(buf);

        return this;
    }
}

package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CActionTakeItems extends CAction
{
    CItemStack stackToMatch = new CItemStack();

    public CActionTakeItems()
    {
    }

    public CActionTakeItems(ItemStack stackToMatch)
    {
        set(stackToMatch);
    }


    public CActionTakeItems set(ItemStack stackToMatch)
    {
        this.stackToMatch.set(stackToMatch);
        return this;
    }


    @Override
    public void execute(Entity entity)
    {
        if (!(entity instanceof EntityPlayerMP)) return;

        int i = stackToMatch.stack.getCount();
        for (ItemStack stack : ((EntityPlayerMP) entity).inventory.mainInventory)
        {
            if (ItemMatcher.stacksMatch(stack, stackToMatch.stack))
            {
                int shrink = Tools.min(i, stack.getCount());
                stack.shrink(shrink);
                i -= shrink;
                if (i == 0) return;
            }
        }
    }

    @Override
    public CActionTakeItems write(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionTakeItems read(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CActionTakeItems save(OutputStream stream) throws IOException
    {
        stackToMatch.save(stream);

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CActionTakeItems load(InputStream stream) throws IOException
    {
        stackToMatch.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }
}

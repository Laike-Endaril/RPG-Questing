package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.items.ItemMatcher;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionHaveItems extends CCondition
{
    public CItemStack stackToMatch = new CItemStack();

    public CConditionHaveItems()
    {
    }

    public CConditionHaveItems(ItemStack stackToMatch)
    {
        set(stackToMatch);
    }

    public CConditionHaveItems set(ItemStack stackToMatch)
    {
        this.stackToMatch.set(stackToMatch);
        return this;
    }

    @Override
    public ArrayList<String> unmetConditions(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!(entity instanceof EntityPlayerMP)) result.add("Entity must be a player");
        else
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            int i = 0;
            for (ItemStack stack : player.inventory.mainInventory)
            {
                if (ItemMatcher.stacksMatch(stack, stackToMatch.stack)) i += stack.getCount();
                if (i >= stackToMatch.stack.getCount()) return result;
            }
            result.add("You need at least " + stackToMatch.stack.getCount() + " " + stackToMatch.stack.getDisplayName() + " in your inventory");
        }
        return result;
    }

    @Override
    public String description()
    {
        return "Requires player to have items: " + TextFormatting.GOLD + "(" + stackToMatch.stack.getCount() + "x) " + stackToMatch.stack.getDisplayName();
    }

    @Override
    public CConditionHaveItems write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionHaveItems read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionHaveItems save(OutputStream stream)
    {
        stackToMatch.save(stream);
        return this;
    }

    @Override
    public CConditionHaveItems load(InputStream stream)
    {
        stackToMatch.load(stream);
        return this;
    }
}

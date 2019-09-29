package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CConditionInventorySpace extends CCondition
{
    public CInt slotCount = new CInt();

    public CConditionInventorySpace()
    {
    }

    public CConditionInventorySpace(int slotCount)
    {
        set(slotCount);
    }

    public CConditionInventorySpace set(int slotCount)
    {
        this.slotCount.set(slotCount);
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
                if (stack.isEmpty() && ++i >= slotCount.value) return result;
            }
            result.add("You need at least " + slotCount.value + " inventory space" + (slotCount.value == 1 ? "" : "s") + " available");
        }
        return result;
    }

    @Override
    public String description()
    {
        return "Requires player have empty inventory space" + (slotCount.value == 1 ? "" : "s" + ": " + TextFormatting.GOLD + slotCount.value);
    }

    @Override
    public CConditionInventorySpace write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionInventorySpace read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CConditionInventorySpace save(OutputStream stream)
    {
        slotCount.save(stream);
        return this;
    }

    @Override
    public CConditionInventorySpace load(InputStream stream)
    {
        slotCount.load(stream);
        return this;
    }
}

package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.conditions.gui.GUICondition;
import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

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
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Inventory space: " + slotCount.value);
        return result;
    }

    @Override
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        GUICondition conditionElement = new GUICondition(screen, new CConditionInventorySpace(2));
        conditionElement.text = conditionElement.text.replace("2", "x");
        return conditionElement;
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

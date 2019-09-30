package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.rpgquesting.conditions.gui.GUICondition;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

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
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Items: " + stackToMatch.stack.getCount() + " " + stackToMatch.stack.getDisplayName());
        return result;
    }

    @Override
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        return new GUICondition(screen, new CConditionHaveItems(ItemStack.EMPTY));
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

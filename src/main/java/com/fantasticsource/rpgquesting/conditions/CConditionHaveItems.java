package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.rpgquesting.gui.GUICondition;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
                if (ItemMatcher.stacksMatch(stack, stackToMatch.value)) i += stack.getCount();
                if (i >= stackToMatch.value.getCount()) return result;
            }
            result.add("You need at least " + stackToMatch.value.getCount() + " " + stackToMatch.value.getDisplayName() + " in your inventory");
        }
        return result;
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Items: " + stackToMatch.value.getCount() + " " + stackToMatch.value.getDisplayName());
        return result;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUICondition getChoosableElement(GUIScreen screen)
    {
        GUICondition element = new GUICondition(screen, new CConditionHaveItems(ItemStack.EMPTY));
        element.text = element.text.replace("0 Air", "???");
        return element;
    }

    @Override
    public CConditionHaveItems write(ByteBuf buf)
    {
        stackToMatch.write(buf);

        return this;
    }

    @Override
    public CConditionHaveItems read(ByteBuf buf)
    {
        stackToMatch.read(buf);

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

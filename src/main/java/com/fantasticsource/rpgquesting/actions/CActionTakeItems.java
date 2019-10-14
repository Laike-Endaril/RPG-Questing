package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.gui.GUIAction;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CActionTakeItems extends CAction
{
    public CItemStack stackToMatch = new CItemStack();

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
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();
        result.add("Take items: " + stackToMatch.stack.getCount() + " " + stackToMatch.stack.getDisplayName());
        return result;
    }

    @Override
    public CActionTakeItems write(ByteBuf buf)
    {
        stackToMatch.write(buf);

        new CInt().set(conditions.size()).write(buf);
        for (CCondition condition : conditions) Component.writeMarked(buf, condition);

        return this;
    }

    @Override
    public CActionTakeItems read(ByteBuf buf)
    {
        stackToMatch.read(buf);

        conditions.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--) conditions.add((CCondition) Component.readMarked(buf));

        return this;
    }

    @Override
    public CActionTakeItems save(OutputStream stream)
    {
        stackToMatch.save(stream);

        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CActionTakeItems load(InputStream stream)
    {
        stackToMatch.load(stream);

        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }

    @Override
    public GUIAction getChoosableElement(GUIScreen screen)
    {
        return new GUIAction(screen, new CActionTakeItems(new ItemStack(Items.AIR, 1)));
    }
}

package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.rpgquesting.gui.GUIAction;
import com.fantasticsource.tools.Tools;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

        int i = stackToMatch.value.getCount();
        for (ItemStack stack : ((EntityPlayerMP) entity).inventory.mainInventory)
        {
            if (ItemMatcher.stacksMatch(stack, stackToMatch.value))
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
        result.add("Take items: " + stackToMatch.value.getCount() + " " + stackToMatch.value.getDisplayName());
        return result;
    }

    @Override
    public CActionTakeItems write(ByteBuf buf)
    {
        super.write(buf);

        stackToMatch.write(buf);

        return this;
    }

    @Override
    public CActionTakeItems read(ByteBuf buf)
    {
        super.read(buf);

        stackToMatch.read(buf);

        return this;
    }

    @Override
    public CActionTakeItems save(OutputStream stream)
    {
        super.save(stream);

        stackToMatch.save(stream);

        return this;
    }

    @Override
    public CActionTakeItems load(InputStream stream)
    {
        super.load(stream);

        stackToMatch.load(stream);

        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIAction getChoosableElement(GUIScreen screen)
    {
        return new GUIAction(screen, new CActionTakeItems(new ItemStack(Items.AIR, 1)));
    }
}

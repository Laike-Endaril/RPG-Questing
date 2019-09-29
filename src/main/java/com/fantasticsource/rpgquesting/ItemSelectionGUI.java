package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIItemStack;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemSelectionGUI extends GUIScreen
{
    public static ItemSelectionGUI GUI;

    private static GUIScrollView scrollView;
    private static GUIItemStack clickedElement;

    static
    {
        GUI = new ItemSelectionGUI();
        MinecraftForge.EVENT_BUS.register(ItemSelectionGUI.class);
    }

    public static void show(GUIItemStack clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(GUI);
        else Minecraft.getMinecraft().displayGuiScreen(GUI);

        ItemSelectionGUI.clickedElement = clickedElement;


        scrollView.clear();

        scrollView.add(new GUIText(GUI, "\n"));

        GUIItemStack stackElement = new GUIItemStack(GUI, clickedElement.getStack().copy());
        stackElement.text += TextFormatting.RESET + " (currently selected)";
        scrollView.add(stackElement);
        scrollView.add(new GUIText(GUI, "\n\n"));

        stackElement = new GUIItemStack(GUI, ItemStack.EMPTY);
        stackElement.text = "(Remove item)";
        scrollView.add(stackElement);
        scrollView.add(new GUIText(GUI, "\n\n\n"));

        EntityPlayer player = Minecraft.getMinecraft().player;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            scrollView.add(new GUIItemStack(GUI, player.inventory.getStackInSlot(i).copy()));
            scrollView.add(new GUIText(GUI, "\n"));
        }
    }

    @Override
    protected void init()
    {
        drawStack = false;

        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        scrollView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        guiElements.add(scrollView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
    }

    @SubscribeEvent
    public static void click(GUILeftClickEvent event)
    {
        if (Minecraft.getMinecraft().currentScreen != GUI) return;

        GUIElement element = event.getElement();
        if (element instanceof GUIItemStack)
        {
            clickedElement.setStack(((GUIItemStack) element).getStack().copy());
            GUI.close();
        }
    }
}

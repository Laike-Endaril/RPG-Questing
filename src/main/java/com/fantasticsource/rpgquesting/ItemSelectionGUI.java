package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIItemStack;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ItemSelectionGUI extends GUIScreen
{
    private GUIScrollView scrollView;
    private GUIItemStack clickedElement;

    public static void show(GUIItemStack clickedElement)
    {
        ItemSelectionGUI gui = new ItemSelectionGUI();

        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(gui);
        else Minecraft.getMinecraft().displayGuiScreen(gui);

        gui.clickedElement = clickedElement;

        gui.scrollView.add(new GUIText(gui, "\n"));

        gui.scrollView.add(new GUIItemStack(gui, clickedElement.stack.copy()));
        gui.scrollView.add(new GUIText(gui, "\n"));

        EntityPlayer player = Minecraft.getMinecraft().player;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            gui.scrollView.add(new GUIItemStack(gui, player.inventory.getStackInSlot(i).copy()));
            gui.scrollView.add(new GUIText(gui, "\n"));
        }
    }

    @Override
    protected void init()
    {
        drawStack = false;

        scrollView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        guiElements.add(scrollView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
    }
}

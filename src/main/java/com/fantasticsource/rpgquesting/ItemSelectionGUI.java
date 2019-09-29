package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIItemStack;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.quest.QuestEditorGUI;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.rpgquesting.quest.JournalGUI.RED;

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
        stackElement.text += "" + TextFormatting.RESET + TextFormatting.DARK_PURPLE + " (currently selected)";
        scrollView.add(stackElement);
        scrollView.add(new GUIText(GUI, "\n\n"));

        stackElement = new GUIItemStack(GUI, ItemStack.EMPTY);
        stackElement.text = TextFormatting.DARK_PURPLE + "(Remove item)";
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
            ItemStack stack = ((GUIItemStack) element).getStack();
            if (clickedElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new reward)"))
            {
                if (!stack.isEmpty())
                {
                    int index = QuestEditorGUI.rewards.indexOf(clickedElement);
                    QuestEditorGUI.rewards.add(index, new GUIText(QuestEditorGUI.GUI, "\n"));
                    QuestEditorGUI.rewards.add(index, new GUIItemStack(QuestEditorGUI.GUI, stack.copy()));

                    if (index == 1) //Rewards were empty, but no longer are
                    {
                        QuestEditorGUI.rewards.add(new GUIText(QuestEditorGUI.GUI, "(Clear all rewards)\n", RED[0], RED[1], RED[2]));
                        QuestEditorGUI.rewards.add(new GUIText(QuestEditorGUI.GUI, "\n"));
                    }
                }
            }
            else clickedElement.setStack(stack.copy());

            GUI.close();
        }
    }
}

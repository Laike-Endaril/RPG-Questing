package com.fantasticsource.rpgquesting.selectionguis;

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

import static com.fantasticsource.rpgquesting.quest.JournalGUI.RED;

public class RewardSelectionGUI extends GUIScreen
{
    public static final RewardSelectionGUI GUI = new RewardSelectionGUI();

    private static GUIScrollView scrollView;
    private static GUIItemStack clickedElement;

    public static void show(GUIItemStack clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(GUI);
        else Minecraft.getMinecraft().displayGuiScreen(GUI);

        RewardSelectionGUI.clickedElement = clickedElement;


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
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                scrollView.add(new GUIItemStack(GUI, stack.copy()));
                scrollView.add(new GUIText(GUI, "\n"));
            }
        }

        for (int i = scrollView.size() - 1; i >= 0; i--)
        {
            GUIElement element = scrollView.get(i);
            if (element instanceof GUIItemStack)
            {
                element.addClickActions(() -> doIt(((GUIItemStack) element).getStack()));
            }
        }
    }

    public static void doIt(ItemStack stack)
    {
        if (clickedElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new reward)"))
        {
            //Started with empty slot
            if (!stack.isEmpty())
            {
                //Added new reward
                int index = QuestEditorGUI.rewards.indexOf(clickedElement);
                QuestEditorGUI.rewards.add(index, new GUIText(QuestEditorGUI.GUI, "\n"));
                GUIItemStack rewardElement = new GUIItemStack(QuestEditorGUI.GUI, stack.copy());
                QuestEditorGUI.rewards.add(index, rewardElement.addClickActions(() -> RewardSelectionGUI.show(rewardElement)));

                if (index == 1)
                {
                    //Rewards were empty, but no longer are
                    QuestEditorGUI.rewards.add(new GUIText(GUI, "(Clear all rewards)\n", RED[0], RED[1], RED[2]).addClickActions(() ->
                    {
                        QuestEditorGUI.rewards.clear();

                        QuestEditorGUI.rewards.add(new GUIText(GUI, "\n"));
                        GUIItemStack rewardElement2 = new GUIItemStack(GUI, ItemStack.EMPTY);
                        rewardElement2.text = TextFormatting.DARK_PURPLE + "(Add new reward)";
                        QuestEditorGUI.rewards.add(rewardElement2.addClickActions(() -> RewardSelectionGUI.show(rewardElement2)));

                        QuestEditorGUI.rewards.add(new GUIText(GUI, "\n"));
                    }));
                    QuestEditorGUI.rewards.add(new GUIText(QuestEditorGUI.GUI, "\n"));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (!stack.isEmpty()) clickedElement.setStack(stack.copy());
            else
            {
                //Removing a reward
                int index = QuestEditorGUI.rewards.indexOf(clickedElement);
                QuestEditorGUI.rewards.remove(index);
                QuestEditorGUI.rewards.remove(index);

                if (QuestEditorGUI.rewards.size() == 5)
                {
                    //Had one reward, and now have 0 (remove the "clear all" option)
                    QuestEditorGUI.rewards.remove(3);
                    QuestEditorGUI.rewards.remove(3);
                }
            }
        }

        GUI.close();
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
}

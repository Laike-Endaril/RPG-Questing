package com.fantasticsource.rpgquesting.selectionguis;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.conditions.*;
import com.fantasticsource.rpgquesting.conditions.quest.CConditionQuestAvailable;
import com.fantasticsource.rpgquesting.conditions.quest.CConditionQuestCompleted;
import com.fantasticsource.rpgquesting.conditions.quest.CConditionQuestInProgress;
import com.fantasticsource.rpgquesting.conditions.quest.CConditionQuestReadyToComplete;
import com.fantasticsource.rpgquesting.quest.QuestEditorGUI;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.rpgquesting.quest.JournalGUI.RED;

public class ConditionSelectionGUI extends GUIScreen
{
    public static ConditionSelectionGUI GUI;

    private static GUIScrollView scrollView;
    private static GUICondition clickedElement;

    static
    {
        GUI = new ConditionSelectionGUI();
        MinecraftForge.EVENT_BUS.register(ConditionSelectionGUI.class);
    }

    public static void show(GUICondition clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(GUI);
        else Minecraft.getMinecraft().displayGuiScreen(GUI);

        ConditionSelectionGUI.clickedElement = clickedElement;


        scrollView.clear();

        //Current
        scrollView.add(new GUIText(GUI, "\n"));
        GUICondition conditionElement = new GUICondition(GUI, clickedElement.condition == null ? null : (CCondition) clickedElement.condition.copy());
        conditionElement.text += "" + TextFormatting.RESET + TextFormatting.DARK_PURPLE + " (currently selected)";
        scrollView.add(conditionElement);
        scrollView.add(new GUIText(GUI, "\n\n"));

        //Remove
        scrollView.add(new GUIText(GUI, "\n"));
        conditionElement = new GUICondition(GUI, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Remove condition)";
        scrollView.add(conditionElement);
        scrollView.add(new GUIText(GUI, "\n\n"));

        //Quest conditions
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionQuestAvailable().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionQuestInProgress().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionQuestReadyToComplete().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionQuestCompleted().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        //Normal conditions
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionNameIs().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionEntityEntryIs().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionClassIs().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionInventorySpace().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionHaveItems().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        //Meta conditions
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionAnd().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionNot().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));

        scrollView.add(new CConditionOr().getChoosableElement(GUI));
        scrollView.add(new GUIText(GUI, "\n"));
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
        if (event.getScreen() != GUI) return;


        GUIElement element = event.getElement();
        if (element instanceof GUICondition)
        {
            CCondition condition = ((GUICondition) element).condition;
            if (clickedElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
            {
                //Started with empty slot
                if (condition != null)
                {
                    //Added new condition
                    int index = QuestEditorGUI.conditions.indexOf(clickedElement);
                    QuestEditorGUI.conditions.add(index, new GUIText(QuestEditorGUI.GUI, "\n"));
                    QuestEditorGUI.conditions.add(index, new GUICondition(QuestEditorGUI.GUI, (CCondition) condition.copy()));

                    if (index == 1)
                    {
                        //Conditions were empty, but no longer are
                        QuestEditorGUI.conditions.add(new GUIText(QuestEditorGUI.GUI, "(Clear all conditions)\n", RED[0], RED[1], RED[2]));
                        QuestEditorGUI.conditions.add(new GUIText(QuestEditorGUI.GUI, "\n"));
                    }
                }
            }
            else
            {
                //Started with non-empty slot, or at least one that should not be empty
                if (condition != null) clickedElement.setCondition((CCondition) condition.copy());
                else
                {
                    //Removing a condition
                    int index = QuestEditorGUI.conditions.indexOf(clickedElement);
                    QuestEditorGUI.conditions.remove(index);
                    QuestEditorGUI.conditions.remove(index);

                    if (QuestEditorGUI.conditions.size() == 5)
                    {
                        //Had one condition, and now have 0 (remove the "clear all" option)
                        QuestEditorGUI.conditions.remove(3);
                        QuestEditorGUI.conditions.remove(3);
                    }
                }
            }

            GUI.close();
        }
    }
}

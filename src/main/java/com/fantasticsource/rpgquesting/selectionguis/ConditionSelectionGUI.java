package com.fantasticsource.rpgquesting.selectionguis;

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

import static com.fantasticsource.rpgquesting.quest.JournalGUI.RED;

public class ConditionSelectionGUI extends GUIScreen
{
    public static final ConditionSelectionGUI GUI = new ConditionSelectionGUI();

    private static GUIScrollView scrollView;
    private static GUICondition clickedElement;

    public static void show(GUICondition clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(GUI);
        else Minecraft.getMinecraft().displayGuiScreen(GUI);

        ConditionSelectionGUI.clickedElement = clickedElement;


        scrollView.clear();

        {
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
        }

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

        for (int i = scrollView.size() - 1; i >= 0; i--)
        {
            GUIElement element = scrollView.get(i);
            if (element instanceof GUICondition)
            {
                element.addClickActions(() -> doIt(((GUICondition) element).condition));
            }
        }
    }

    private static void doIt(CCondition condition)
    {
        if (clickedElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
        {
            //Started with empty slot
            if (condition != null)
            {
                //Added new condition
                int index = QuestEditorGUI.conditions.indexOf(clickedElement);
                QuestEditorGUI.conditions.add(index, new GUIText(QuestEditorGUI.GUI, "\n"));
                GUICondition conditionElement = new GUICondition(QuestEditorGUI.GUI, (CCondition) condition.copy());
                QuestEditorGUI.conditions.add(index, conditionElement.addClickActions(() -> ConditionSelectionGUI.show(conditionElement)));

                if (index == 1)
                {
                    //Conditions were empty, but no longer are
                    QuestEditorGUI.conditions.add(new GUIText(GUI, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(() ->
                    {
                        QuestEditorGUI.conditions.clear();

                        QuestEditorGUI.conditions.add(new GUIText(GUI, "\n"));
                        GUICondition conditionElement2 = new GUICondition(GUI, null);
                        conditionElement2.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
                        QuestEditorGUI.conditions.add(conditionElement2.addClickActions(() -> ConditionSelectionGUI.show(conditionElement2)));

                        QuestEditorGUI.conditions.add(new GUIText(GUI, "\n"));
                    }));
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

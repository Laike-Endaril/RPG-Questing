package com.fantasticsource.rpgquesting.conditions.gui;

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
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public class ConditionEditorGUI extends GUIScreen
{
    public CCondition selection;

    public ConditionEditorGUI(GUICondition clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        selection = clickedElement.condition;


        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        GUIScrollView conditionSelector = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        guiElements.add(conditionSelector);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionSelector));

        //Current
        conditionSelector.add(new GUIText(this, "\n"));
        GUICondition conditionElement = new GUICondition(this, clickedElement.condition == null ? null : (CCondition) clickedElement.condition.copy());
        conditionElement.text += "" + TextFormatting.RESET + TextFormatting.DARK_PURPLE + " (currently selected)";
        conditionSelector.add(conditionElement);
        conditionSelector.add(new GUIText(this, "\n\n"));

        //Remove
        conditionSelector.add(new GUIText(this, "\n"));
        conditionElement = new GUICondition(this, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Remove condition)";
        conditionSelector.add(conditionElement);
        conditionSelector.add(new GUIText(this, "\n\n"));

        //Quest conditions
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionQuestAvailable().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionQuestInProgress().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionQuestReadyToComplete().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionQuestCompleted().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        //Normal conditions
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionNameIs().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionEntityEntryIs().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionClassIs().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionInventorySpace().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionHaveItems().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        //Meta conditions
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionAnd().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionNot().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        conditionSelector.add(new CConditionOr().getChoosableElement(this));
        conditionSelector.add(new GUIText(this, "\n"));

        for (int i = conditionSelector.size() - 1; i >= 0; i--)
        {
            GUIElement element = conditionSelector.get(i);
            if (element instanceof GUICondition)
            {
                element.addClickActions(() ->
                {
                    selection = ((GUICondition) element).condition;
                    close();
                });
            }
        }
    }

    @Override
    protected void init()
    {
    }
}

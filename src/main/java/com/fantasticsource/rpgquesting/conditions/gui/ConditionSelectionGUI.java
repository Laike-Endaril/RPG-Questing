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

public class ConditionSelectionGUI extends GUIScreen
{
    public CCondition selection;

    public ConditionSelectionGUI(GUICondition clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        selection = clickedElement.condition;


        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        GUIScrollView scrollView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        guiElements.add(scrollView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));

        //Current
        scrollView.add(new GUIText(this, "\n"));
        GUICondition conditionElement = new GUICondition(this, clickedElement.condition == null ? null : (CCondition) clickedElement.condition.copy());
        conditionElement.text += "" + TextFormatting.RESET + TextFormatting.DARK_PURPLE + " (currently selected)";
        scrollView.add(conditionElement);
        scrollView.add(new GUIText(this, "\n\n"));

        //Remove
        scrollView.add(new GUIText(this, "\n"));
        conditionElement = new GUICondition(this, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Remove condition)";
        scrollView.add(conditionElement);
        scrollView.add(new GUIText(this, "\n\n"));

        //Quest conditions
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionQuestAvailable().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionQuestInProgress().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionQuestReadyToComplete().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionQuestCompleted().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        //Normal conditions
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionNameIs().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionEntityEntryIs().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionClassIs().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionInventorySpace().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionHaveItems().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        //Meta conditions
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionAnd().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionNot().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        scrollView.add(new CConditionOr().getChoosableElement(this));
        scrollView.add(new GUIText(this, "\n"));

        for (int i = scrollView.size() - 1; i >= 0; i--)
        {
            GUIElement element = scrollView.get(i);
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

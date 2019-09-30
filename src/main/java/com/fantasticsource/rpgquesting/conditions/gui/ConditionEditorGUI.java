package com.fantasticsource.rpgquesting.conditions.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.conditions.*;
import com.fantasticsource.rpgquesting.conditions.quest.CConditionQuestAvailable;
import com.fantasticsource.rpgquesting.conditions.quest.CConditionQuestCompleted;
import com.fantasticsource.rpgquesting.conditions.quest.CConditionQuestInProgress;
import com.fantasticsource.rpgquesting.conditions.quest.CConditionQuestReadyToComplete;
import com.fantasticsource.rpgquesting.quest.JournalGUI;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.quest.JournalGUI.RED;

public class ConditionEditorGUI extends GUIScreen
{
    public CCondition original, selection;
    public GUICondition current;
    private GUIGradientBorder separator;
    private GUIScrollView conditionSelector, conditionEditor;
    private GUIVerticalScrollbar conditionSelectorScroll, conditionEditorScroll;

    public ConditionEditorGUI(GUICondition clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.condition;
        selection = original;


        GUIGradient root = new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f));
        guiElements.add(root);


        //Management
        current = new GUICondition(this, original);
        GUITextButton save = new GUITextButton(this, "Save and Close", JournalGUI.GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            selection = current.condition;
            close();
        }));

        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel.addClickActions(() ->
        {
            selection = original;
            close();
        }));

        GUITextButton delete = new GUITextButton(this, "Delete Condition and Close", RED[0]);
        root.add(delete.addClickActions(() ->
        {
            selection = null;
            close();
        }));


        //Before and after
        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));

        root.add(new GUIText(this, TextFormatting.GOLD + "Original: "));
        GUICondition originalElement = new GUICondition(this, current.condition == null ? null : (CCondition) current.condition.copy());
        root.add(originalElement.addClickActions(() -> setCurrent(originalElement.condition)));
        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));

        root.add(new GUIText(this, TextFormatting.GOLD + "Current: "));
        root.add(current);
        separator = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separator);


        //Condition selector
        double yy = separator.y + separator.height, yy2 = yy + (1 - yy) / 2;
        conditionSelector = new GUIScrollView(this, 0.02, yy, 0.94, yy2 - yy);
        root.add(conditionSelector);

        conditionSelectorScroll = new GUIVerticalScrollbar(this, 0.98, yy, 0.02, yy2 - yy, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionSelector);
        root.add(conditionSelectorScroll);

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
                element.addClickActions(() -> setCurrent(((GUICondition) element).condition));
            }
        }


        //Condition editor
        conditionEditor = new GUIScrollView(this, 0.02, yy2, 0.94, 1 - yy2);
        root.add(conditionEditor);

        conditionEditorScroll = new GUIVerticalScrollbar(this, 0.98, yy2, 0.02, 1 - yy2, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionEditor);
        root.add(conditionEditorScroll);

        setCurrent(current.condition);
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);

        double yy = separator.y + separator.height, yy2 = yy + (1 - yy) / 2;
        conditionSelector.y = yy;
        conditionSelector.height = yy2 - yy;
        conditionSelector.recalc();

        conditionSelectorScroll.y = yy;
        conditionSelectorScroll.height = yy2 - yy;
        conditionSelectorScroll.recalc();


        conditionEditor.y = yy2;
        conditionEditor.height = 1 - yy2;
        conditionEditor.recalc();

        conditionEditorScroll.y = yy2;
        conditionEditorScroll.height = 1 - yy2;
        conditionEditorScroll.recalc();
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CCondition condition)
    {
        current.setCondition(condition);


        conditionEditor.clear();

        if (condition != null)
        {
            Class cls = condition.getClass();
            if (cls == CConditionNameIs.class)
            {
                GUILabeledTextInput name = new GUILabeledTextInput(this, "Name: ", ((CConditionNameIs) condition).name.value, FilterNotEmpty.INSTANCE);
                name.input.addRecalcActions(() ->
                {
                    ((CConditionNameIs) condition).name.set(name.input.text);
                    current.setCondition(condition);
                });
                conditionEditor.add(name);
            }
        }
    }
}

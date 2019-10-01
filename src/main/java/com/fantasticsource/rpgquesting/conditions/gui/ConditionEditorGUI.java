package com.fantasticsource.rpgquesting.conditions.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIItemStack;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.screen.ItemSelectionGUI;
import com.fantasticsource.rpgquesting.conditions.*;
import com.fantasticsource.rpgquesting.conditions.quest.*;
import com.fantasticsource.rpgquesting.quest.JournalGUI;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.quest.JournalGUI.RED;

public class ConditionEditorGUI extends GUIScreen
{
    public CCondition original, selection;
    public GUICondition current;
    private GUIGradient root;
    private GUIGradientBorder[] separators = new GUIGradientBorder[4];
    private GUIScrollView conditionSelector, conditionEditor, originalView, currentView;
    private GUIVerticalScrollbar conditionSelectorScrollbar, conditionEditorScrollbar, originalScrollbar, currentScrollbar;

    public ConditionEditorGUI(GUICondition clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.condition;
        selection = original;


        root = new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f));
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


        separators[0] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[0]);


        //Original
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalView = new GUIScrollView(this, 0.44, 1d / 6);
        root.add(originalView);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalScrollbar = new GUIVerticalScrollbar(this, 0.02, 1d / 6, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, originalView);
        root.add(originalScrollbar);

        originalView.add(new GUIText(this, "\n" + TextFormatting.GOLD + "Original:\n\n"));
        GUICondition originalElement = new GUICondition(this, current.condition == null ? null : (CCondition) current.condition.copy());
        originalView.add(originalElement.addClickActions(() -> setCurrent(originalElement.condition)));
        originalView.add(new GUIText(this, "\n"));


        //Current
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        currentView = new GUIScrollView(this, 0.44, 1d / 6);
        root.add(currentView);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        currentScrollbar = new GUIVerticalScrollbar(this, 0.02, 1d / 6, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, currentView);
        root.add(currentScrollbar);

        currentView.add(new GUIText(this, "\n" + TextFormatting.GOLD + "Current:\n\n"));
        currentView.add(current);
        currentView.add(new GUIText(this, "\n"));


        separators[2] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[2]);


        //Condition selector
        double yy = separators[2].y + separators[2].height, yy2 = yy + (1 - yy) / 2;

        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        conditionSelector = new GUIScrollView(this, 0.94, yy2 - yy);
        root.add(conditionSelector);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        conditionSelectorScrollbar = new GUIVerticalScrollbar(this, 0.02, yy2 - yy, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionSelector);
        root.add(conditionSelectorScrollbar);

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


        separators[3] = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separators[3]);


        //Condition editor
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        conditionEditor = new GUIScrollView(this, 0.94, 1 - yy2 - separators[3].height);
        root.add(conditionEditor);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));

        conditionEditorScrollbar = new GUIVerticalScrollbar(this, 0.02, 1 - yy2 - separators[3].height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, conditionEditor);
        root.add(conditionEditorScrollbar);

        setCurrent(current.condition);
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);

        double yy = separators[2].y + separators[2].height, yy2 = yy + (1 - yy) / 2;

        conditionSelector.height = yy2 - yy;
        conditionSelectorScrollbar.height = yy2 - yy;


        conditionEditor.height = 1 - yy2 - separators[3].height;
        conditionEditorScrollbar.height = 1 - yy2 - separators[3].height;

        root.recalc();
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CCondition condition)
    {
        current.setCondition(condition);


        conditionEditor.clear();

        conditionEditor.add(new GUIText(this, "\n"));

        if (condition != null)
        {
            Class cls = condition.getClass();
            if (cls == CConditionNameIs.class)
            {
                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity name: ", ((CConditionNameIs) condition).name.value, FilterNotEmpty.INSTANCE);
                name.input.addRecalcActions(() ->
                {
                    if (name.input.valid())
                    {
                        ((CConditionNameIs) condition).name.set(name.input.text);
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(name);
                conditionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CConditionEntityEntryIs.class)
            {
                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity registry name: ", ((CConditionEntityEntryIs) condition).entityEntryName.value, FilterNotEmpty.INSTANCE);
                name.input.addRecalcActions(() ->
                {
                    if (name.input.valid())
                    {
                        ((CConditionEntityEntryIs) condition).entityEntryName.set(name.input.text);
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(name);
                conditionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CConditionClassIs.class)
            {
                GUILabeledTextInput name = new GUILabeledTextInput(this, "Entity class name: ", ((CConditionClassIs) condition).className.value, FilterNotEmpty.INSTANCE);
                name.input.addRecalcActions(() ->
                {
                    if (name.input.valid())
                    {
                        ((CConditionClassIs) condition).className.set(name.input.text);
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(name);
                conditionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CConditionInventorySpace.class)
            {
                GUILabeledTextInput slotCount = new GUILabeledTextInput(this, "Empty slot count: ", "" + ((CConditionInventorySpace) condition).slotCount.value, FilterInt.INSTANCE);
                slotCount.input.addRecalcActions(() ->
                {
                    if (slotCount.input.valid())
                    {
                        ((CConditionInventorySpace) condition).slotCount.set(Integer.parseInt(slotCount.input.text));
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(slotCount);
                conditionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CConditionHaveItems.class)
            {
                CConditionHaveItems haveItems = (CConditionHaveItems) condition;
                GUIItemStack stackElement = new GUIItemStack(this, haveItems.stackToMatch.stack);
                conditionEditor.add(stackElement.addClickActions(() ->
                {
                    ItemSelectionGUI gui = new ItemSelectionGUI(stackElement);
                    gui.addOnClosedActions(() ->
                    {
                        stackElement.setStack(gui.selection);
                        haveItems.set(gui.selection);
                        current.setCondition(haveItems);
                    });
                }));
                conditionEditor.add(new GUIText(this, "\n"));
            }
            else if (condition instanceof CQuestCondition)
            {
                GUILabeledTextInput questName = new GUILabeledTextInput(this, "Quest name: ", "" + ((CQuestCondition) condition).name.value, FilterNotEmpty.INSTANCE);
                questName.input.addRecalcActions(() ->
                {
                    if (questName.input.valid())
                    {
                        ((CQuestCondition) condition).name.set(questName.input.text);
                        current.setCondition(condition);
                    }
                });
                conditionEditor.add(questName);
                conditionEditor.add(new GUIText(this, "\n"));
            }
            else if (cls == CConditionNot.class)
            {
                CConditionNot not = (CConditionNot) condition;
                GUICondition subCondition = new GUICondition(this, not.condition);
                conditionEditor.add(subCondition.addClickActions(() ->
                {
                    ConditionEditorGUI gui = new ConditionEditorGUI(subCondition);
                    gui.addOnClosedActions(() ->
                    {
                        subCondition.setCondition(gui.selection);
                        not.condition = gui.selection;
                        current.setCondition(not);
                    });
                }));
                conditionEditor.add(new GUIText(this, "\n"));
            }
        }

        currentView.recalc();
    }
}

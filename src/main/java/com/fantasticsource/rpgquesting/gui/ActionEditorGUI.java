package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.actions.*;
import com.fantasticsource.rpgquesting.actions.quest.CActionCompleteQuest;
import com.fantasticsource.rpgquesting.actions.quest.CActionStartQuest;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class ActionEditorGUI extends GUIScreen
{
    public CAction original, selection;
    public GUIAction current;
    private GUITextButton delete;
    private GUIText originalLabel, currentLabel;
    private GUIScrollView actionSelector, actionEditor, originalView, currentView;
    private GUIVerticalScrollbar actionSelectorScrollbar, actionEditorScrollbar, originalScrollbar, currentScrollbar;

    public ActionEditorGUI(GUIAction clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.action;
        selection = original;


        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));


        //Management
        current = new GUIAction(this, original == null ? null : (CAction) original.copy());
        GUITextButton save = new GUITextButton(this, "Save and Close", GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            selection = current.action;
            close();
        }));

        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel.addClickActions(() ->
        {
            selection = original;
            close();
        }));

        delete = new GUITextButton(this, "Delete Action and Close", RED[0]);
        root.add(delete.addClickActions(() ->
        {
            selection = null;
            close();
        }));


        double free = 1 - delete.height - 0.01;


        root.add(new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f)));


        //Labels
        originalLabel = new GUIText(this, 0, 0, "ORIGINAL", Color.YELLOW.copy().setVF(0.2f));
        root.add(originalLabel);
        currentLabel = new GUIText(this, 0, 0, "CURRENT", Color.YELLOW.copy().setVF(0.2f));
        root.add(currentLabel);


        //Original
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalView = new GUIScrollView(this, 0.44, free / 3);
        root.add(originalView);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        originalScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, originalView);
        root.add(originalScrollbar);

        originalView.add(new GUIText(this, "\n"));
        GUIAction originalElement = new GUIAction(this, current.action == null ? null : (CAction) current.action.copy());
        originalView.add(originalElement.addClickActions(() -> setCurrent(originalElement.action)));
        originalView.add(new GUIText(this, "\n"));


        //Current
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        currentView = new GUIScrollView(this, 0.44, free / 3);
        root.add(currentView);
        root.add(new GUIGradient(this, 0.02, 0.01, Color.BLANK));
        currentScrollbar = new GUIVerticalScrollbar(this, 0.02, free / 3, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, currentView);
        root.add(currentScrollbar);

        currentView.add(new GUIText(this, "\n"));
        currentView.add(current);
        currentView.add(new GUIText(this, "\n"));


        //Tabview
        GUITabView tabView = new GUITabView(this, 1, free * 2 / 3, "Base Action Type", "Action Options", "Required Conditions");
        root.add(tabView);


        //Base Action Type tab
        actionSelector = new GUIScrollView(this, 0.02, 1, 0.94, 1);
        tabView.tabViews.get(0).add(actionSelector);
        actionSelectorScrollbar = new GUIVerticalScrollbar(this, 0.98, 1, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, actionSelector);
        tabView.tabViews.get(0).add(actionSelectorScrollbar);

        //Dialogue actions
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionBranch().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionEndDialogue().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        //Quest actions
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionStartQuest().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionCompleteQuest().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        //Normal actions
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionTakeItems().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        //Meta actions
        actionSelector.add(new GUIText(this, "\n"));

        actionSelector.add(new CActionArray().getChoosableElement(this));
        actionSelector.add(new GUIText(this, "\n"));

        for (int i = actionSelector.size() - 1; i >= 0; i--)
        {
            GUIElement element = actionSelector.get(i);
            if (element instanceof GUIAction)
            {
                element.addClickActions(() -> setCurrent(((GUIAction) element).action));
            }
        }


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);

        double free = 1 - delete.height - 0.03;


        //Resize views and scrollbars
        originalView.height = free / 3;
        originalScrollbar.height = free / 3;

        currentView.height = free / 3;
        currentScrollbar.height = free / 3;

        actionSelector.height = free / 3;
        actionSelectorScrollbar.height = free / 3;

        actionEditor.height = free / 3;
        actionEditorScrollbar.height = free / 3;


        //Reposition labels
        originalLabel.x = originalView.x + originalView.width / 2 - originalLabel.width / 2;
        originalLabel.y = originalView.y + originalView.height / 2 - originalLabel.height / 2;

        currentLabel.x = currentView.x + currentView.width / 2 - currentLabel.width / 2;
        currentLabel.y = currentView.y + currentView.height / 2 - currentLabel.height / 2;


        root.recalc();
    }

    @Override
    protected void init()
    {
    }

    private void setCurrent(CAction action)
    {
    }
}

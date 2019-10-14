package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogueChoice;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class ChoiceEditorGUI extends GUIScreen
{
    public CDialogueChoice selection;
    private GUITabView tabView;
    private GUIGradientBorder separator;

    public ChoiceEditorGUI(GUIChoice clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        selection = clickedElement.choice;


    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);


        //Resize views and scrollbars
        tabView.y = separator.y + separator.height;
        tabView.height = 1 - tabView.y;


        root.recalc();
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));


        //Management
        GUITextButton save = new GUITextButton(this, "Save and Close", GREEN[0]);
        root.add(save.addClickActions(() ->
        {
            //TODO create new choice and set selection to it
            close();
        }));

        GUITextButton cancel = new GUITextButton(this, "Close Without Saving");
        root.add(cancel.addClickActions(this::close));

        GUITextButton delete = new GUITextButton(this, "Delete Choice and Close", RED[0]);
        root.add(delete.addClickActions(() ->
        {
            selection = null;
            close();
        }));


        separator = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separator);


        tabView = new GUITabView(this, 0, separator.y + separator.height, 1, 1 - separator.y - separator.height, "Main", "Availability Conditions", "Actions");
        root.add(tabView);
    }


    private void editCondition(GUICondition activeConditionElement, CCondition newCondition)
    {
        //TODO
    }

    private void clearConditions()
    {
        //TODO
    }


//    private void editAction(GUIAction activeActionElement, CAction newAction)
//    {
//        //TODO
//    }

    private void clearActions()
    {
        //TODO
    }
}

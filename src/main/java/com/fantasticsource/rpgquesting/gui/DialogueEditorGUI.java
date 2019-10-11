package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import static com.fantasticsource.rpgquesting.Colors.*;

public class DialogueEditorGUI extends GUIScreen
{
    public static final DialogueEditorGUI GUI = new DialogueEditorGUI();

    public GUIScrollView main, playerConditions, entityConditions, branches;
    private GUIGradientBorder separator;
    private GUITabView tabView;
    private GUILabeledTextInput name;
    private GUIText oldName;

    public void show(CDialogue dialogue)
    {
        Minecraft.getMinecraft().displayGuiScreen(this);


        //Main tab
        main.clear();

        main.add(new GUIText(this, "\n"));

        name = new GUILabeledTextInput(this, "Name: ", dialogue.name.value, FilterNotEmpty.INSTANCE);
        main.add(name);
        main.add(new GUIText(this, "\n"));

        oldName = new GUIText(this, "(Previous Name: " + dialogue.name.value + ")", BLUE[0]);
        main.add(oldName);
        main.add(new GUIText(this, "\n\n"));
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);
        tabView.height = 1 - (separator.y + separator.height);
        tabView.recalc();
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        //Management
        root.add(new GUITextButton(this, "Save", GREEN[0])).addClickActions(this::trySave);
        root.add(new GUITextButton(this, "Close Editor").addClickActions(this::close));
        root.add(new GUITextButton(this, "Delete Dialogue", RED[0]).addClickActions(() -> Network.WRAPPER.sendToServer(new Network.RequestDeleteDialoguePacket(oldName.text.substring(0, oldName.text.length() - 1).replace("(Previous Name: ", "")))));

        //Tabview
        separator = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separator);
        tabView = new GUITabView(this, 1, 1 - (separator.y + separator.height), "Main", "Availability Conditions (Player)", "Availability Conditions (Entity)", "Branches");
        root.add(tabView);

        //Main tab
        main = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(0).add(main);
        tabView.tabViews.get(0).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, main));

        //Player Conditions tab
        playerConditions = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(1).add(playerConditions);
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, playerConditions));

        //Entity Conditions tab
        entityConditions = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(2).add(entityConditions);
        tabView.tabViews.get(2).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, entityConditions));

        //Branches tab
        branches = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(3).add(branches);
        tabView.tabViews.get(3).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, branches));
    }


    boolean trySave()
    {
        return true;
    }
}

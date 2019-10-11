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
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.rpgquesting.Colors.*;

public class DialogueEditorGUI extends GUIScreen
{
    public static final DialogueEditorGUI GUI = new DialogueEditorGUI();

    public GUIScrollView main, playerConditions, entityConditions, branches;
    private GUIGradientBorder separator;
    private GUITabView tabView;
    private GUILabeledTextInput name, group;
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

        group = new GUILabeledTextInput(this, "Group: ", dialogue.group.value, FilterNotEmpty.INSTANCE);
        main.add(group);
        main.add(new GUIText(this, "\n"));


        //Availability conditions (player) tab
        playerConditions.clear();

        for (CCondition condition : dialogue.playerConditions)
        {
            playerConditions.add(new GUIText(this, "\n"));
            GUICondition conditionElement = new GUICondition(this, condition);
            playerConditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                gui.addOnClosedActions(() -> editPlayerCondition(conditionElement, gui.selection));
            }));
        }

        {
            playerConditions.add(new GUIText(this, "\n"));
            GUICondition conditionElement = new GUICondition(this, null);
            conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
            playerConditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                gui.addOnClosedActions(() -> editPlayerCondition(conditionElement, gui.selection));
            }));
        }

        if (dialogue.playerConditions.size() > 0)
        {
            playerConditions.add(new GUIText(this, "\n"));
            playerConditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearPlayerConditions));
        }

        playerConditions.add(new GUIText(this, "\n"));


        //Availability conditions (entity) tab
        entityConditions.clear();

        for (CCondition condition : dialogue.entityConditions)
        {
            entityConditions.add(new GUIText(this, "\n"));
            GUICondition conditionElement = new GUICondition(this, condition);
            entityConditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                gui.addOnClosedActions(() -> editEntityCondition(conditionElement, gui.selection));
            }));
        }

        {
            entityConditions.add(new GUIText(this, "\n"));
            GUICondition conditionElement = new GUICondition(this, null);
            conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
            entityConditions.add(conditionElement.addClickActions(() ->
            {
                ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                gui.addOnClosedActions(() -> editEntityCondition(conditionElement, gui.selection));
            }));
        }

        if (dialogue.entityConditions.size() > 0)
        {
            entityConditions.add(new GUIText(this, "\n"));
            entityConditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearEntityConditions));
        }

        entityConditions.add(new GUIText(this, "\n"));
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);
        tabView.height = 1 - (separator.y + separator.height);
        tabView.recalc();
    }

    @Override
    public void onClosed()
    {
        super.onClosed();
        Network.WRAPPER.sendToServer(new Network.RequestEditorDataPacket());
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

        //Availability conditions (player) tab
        playerConditions = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        tabView.tabViews.get(1).add(playerConditions);
        tabView.tabViews.get(1).add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, playerConditions));

        //Availability conditions (entity) tab
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


    private void editPlayerCondition(GUICondition activeConditionElement, CCondition newCondition)
    {
        if (activeConditionElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
        {
            //Started with empty slot
            if (newCondition != null)
            {
                //Added new objective
                int index = playerConditions.indexOf(activeConditionElement);

                {
                    playerConditions.add(index, new GUIText(this, "\n"));
                    GUICondition conditionElement = new GUICondition(this, (CCondition) newCondition.copy());
                    playerConditions.add(index, conditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                        gui.addOnClosedActions(() -> editPlayerCondition(conditionElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Conditions were empty, but no longer are
                    playerConditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearPlayerConditions));
                    playerConditions.add(new GUIText(this, "\n"));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newCondition != null) activeConditionElement.setCondition((CCondition) newCondition.copy());
            else
            {
                //Removing a objective
                int index = playerConditions.indexOf(activeConditionElement);
                playerConditions.remove(index);
                playerConditions.remove(index);

                if (playerConditions.size() == 5)
                {
                    //Had one objective, and now have 0 (remove the "clear all" option)
                    playerConditions.remove(3);
                    playerConditions.remove(3);
                }
            }
        }

        tabView.recalc();
    }

    private void clearPlayerConditions()
    {
        playerConditions.clear();

        playerConditions.add(new GUIText(this, "\n"));
        GUICondition conditionElement = new GUICondition(this, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
        playerConditions.add(conditionElement.addClickActions(() ->
        {
            ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
            gui.addOnClosedActions(() -> editPlayerCondition(conditionElement, gui.selection));
        }));
        playerConditions.add(new GUIText(this, "\n"));

        tabView.recalc();
    }


    private void editEntityCondition(GUICondition activeConditionElement, CCondition newCondition)
    {
        if (activeConditionElement.text.equals(TextFormatting.DARK_PURPLE + "(Add new condition)"))
        {
            //Started with empty slot
            if (newCondition != null)
            {
                //Added new objective
                int index = entityConditions.indexOf(activeConditionElement);

                {
                    entityConditions.add(index, new GUIText(this, "\n"));
                    GUICondition conditionElement = new GUICondition(this, (CCondition) newCondition.copy());
                    entityConditions.add(index, conditionElement.addClickActions(() ->
                    {
                        ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
                        gui.addOnClosedActions(() -> editEntityCondition(conditionElement, gui.selection));
                    }));
                }

                if (index == 1)
                {
                    //Conditions were empty, but no longer are
                    entityConditions.add(new GUIText(this, "(Clear all conditions)\n", RED[0], RED[1], RED[2]).addClickActions(this::clearEntityConditions));
                    entityConditions.add(new GUIText(this, "\n"));
                }
            }
        }
        else
        {
            //Started with non-empty slot, or at least one that should not be empty
            if (newCondition != null) activeConditionElement.setCondition((CCondition) newCondition.copy());
            else
            {
                //Removing a objective
                int index = entityConditions.indexOf(activeConditionElement);
                entityConditions.remove(index);
                entityConditions.remove(index);

                if (entityConditions.size() == 5)
                {
                    //Had one objective, and now have 0 (remove the "clear all" option)
                    entityConditions.remove(3);
                    entityConditions.remove(3);
                }
            }
        }

        tabView.recalc();
    }

    private void clearEntityConditions()
    {
        entityConditions.clear();

        entityConditions.add(new GUIText(this, "\n"));
        GUICondition conditionElement = new GUICondition(this, null);
        conditionElement.text = TextFormatting.DARK_PURPLE + "(Add new condition)";
        entityConditions.add(conditionElement.addClickActions(() ->
        {
            ConditionEditorGUI gui = new ConditionEditorGUI(conditionElement);
            gui.addOnClosedActions(() -> editEntityCondition(conditionElement, gui.selection));
        }));
        entityConditions.add(new GUIText(this, "\n"));

        tabView.recalc();
    }
}

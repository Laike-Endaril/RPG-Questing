package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import static com.fantasticsource.rpgquesting.Colors.GREEN;
import static com.fantasticsource.rpgquesting.Colors.RED;

public class BranchEditorGUI extends GUIScreen
{
    public CDialogueBranch original, selection;
    public GUIBranch current;
    private GUIGradientBorder separator;
    private GUITabView tabView;

    public BranchEditorGUI(GUIBranch clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        original = clickedElement.branch;
        selection = original == null ? null : (CDialogueBranch) original.copy();
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
        root.add(new GUITextButton(this, "Save and Close", GREEN[0]).addClickActions(() ->
        {
            selection = current.branch;
            close();
        }));

        root.add(new GUITextButton(this, "Close Without Saving").addClickActions(() ->
        {
            selection = original;
            close();
        }));

        root.add(new GUITextButton(this, "Delete Branch and Close", RED[0]).addClickActions(() ->
        {
            selection = null;
            close();
        }));


        separator = new GUIGradientBorder(this, 1, 0.01, 0.3, Color.GRAY, Color.GRAY.copy().setAF(0.3f));
        root.add(separator);


        tabView = new GUITabView(this, 0, separator.y + separator.height, 1, 1 - separator.y - separator.height, "Paragraph", "Choices");
        root.add(tabView);
    }
}

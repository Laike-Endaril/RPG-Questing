package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.Colors;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.ChooseDialoguePacket;
import com.fantasticsource.rpgquesting.Network.MultipleDialoguesPacket;
import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import static com.fantasticsource.mctools.gui.element.GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM;

public class DialoguesGUI extends GUIScreen
{
    public static final DialoguesGUI GUI = new DialoguesGUI(RPGQuesting.TEXT_SCALE);
    private static GUIScrollView scrollView;

    private DialoguesGUI(double textScale)
    {
        super(textScale);
    }

    public static void show(MultipleDialoguesPacket packet)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);

        scrollView.clear();
        for (String name : packet.dialogueNames)
        {
            GUITextButton button = new GUITextButton(GUI, name);
            scrollView.add(button.addClickActions(() -> Network.WRAPPER.sendToServer(new ChooseDialoguePacket(name))));
        }
    }

    @Override
    public String title()
    {
        return "Dialogues";
    }

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));

        scrollView = new GUIScrollView(this, 0, 0, 0.98, 1);
        scrollView.setSubElementAutoplaceMethod(AP_CENTERED_H_TOP_TO_BOTTOM);
        root.add(scrollView);
        root.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
    }
}

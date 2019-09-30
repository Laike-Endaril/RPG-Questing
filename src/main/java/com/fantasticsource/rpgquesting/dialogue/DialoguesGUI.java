package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.ChooseDialoguePacket;
import com.fantasticsource.rpgquesting.Network.MultipleDialoguesPacket;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import static com.fantasticsource.mctools.gui.element.GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM;

public class DialoguesGUI extends GUIScreen
{
    public static final DialoguesGUI GUI = new DialoguesGUI();
    private static GUIScrollView scrollView;

    private DialoguesGUI()
    {
    }

    public static void show(MultipleDialoguesPacket packet)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);

        scrollView.clear();
        for (int i = 0; i < packet.dialogueDisplayNames.size(); i++)
        {
            GUITextButton button = new GUITextButton(GUI, packet.dialogueDisplayNames.get(i).value);
            int i2 = i;
            scrollView.add(button.addClickActions(() ->
            {
                CUUID dialogueSessionID = packet.dialogueSessionIDs.get(i2);
                if (dialogueSessionID != null) Network.WRAPPER.sendToServer(new ChooseDialoguePacket(dialogueSessionID));
            }));
        }
    }

    @Override
    protected void init()
    {
        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        scrollView = new GUIScrollView(this, 0, 0, 0.98, 1);
        scrollView.setSubElementAutoplaceMethod(AP_CENTERED_H_TOP_TO_BOTTOM);
        guiElements.add(scrollView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
    }
}

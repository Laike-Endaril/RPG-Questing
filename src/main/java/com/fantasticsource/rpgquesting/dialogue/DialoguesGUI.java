package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.ChooseDialoguePacket;
import com.fantasticsource.rpgquesting.Network.MultipleDialoguesPacket;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;

import static com.fantasticsource.mctools.gui.element.GUIElement.AP_CENTER_V_CENTER_H;

public class DialoguesGUI extends GUIScreen
{
    public static DialoguesGUI GUI;
    private static LinkedHashMap<GUITextButton, CStringUTF8> buttonToSaveName = new LinkedHashMap<>();
    private static GUIScrollView scrollView;

    static
    {
        MinecraftForge.EVENT_BUS.register(DialoguesGUI.class);
        GUI = new DialoguesGUI();
    }

    private DialoguesGUI()
    {
    }

    public static void show(MultipleDialoguesPacket packet)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);

        scrollView.clear();
        buttonToSaveName.clear();
        for (int i = 0; i < packet.dialogueDisplayNames.size(); i++)
        {
            GUITextButton button = new GUITextButton(GUI, packet.dialogueDisplayNames.get(i).value);
            buttonToSaveName.put(button, packet.dialogueSaveNames.get(i));
            scrollView.add(button);
        }
    }

    @SubscribeEvent
    public static void click(GUILeftClickEvent event)
    {
        CStringUTF8 saveName = buttonToSaveName.get(event.getElement());
        if (saveName != null) Network.WRAPPER.sendToServer(new ChooseDialoguePacket(saveName));
    }

    @Override
    protected void init()
    {
        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        scrollView = new GUIScrollView(this, 0, 0, 0.98, 1);
        scrollView.setSubElementAutoplaceMethod(AP_CENTER_V_CENTER_H);
        guiElements.add(scrollView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
    }
}

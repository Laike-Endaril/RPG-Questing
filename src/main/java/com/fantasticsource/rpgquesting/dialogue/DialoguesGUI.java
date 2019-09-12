package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.mctools.gui.element.GUIElement.AP_CENTER_V_CENTER_H;

public class DialoguesGUI extends GUIScreen
{
    public static DialoguesGUI GUI;
    private static GUIScrollView scrollView;
    private static LinkedHashMap<GUIElement, CDialogue> dialogueLinks = new LinkedHashMap<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(DialoguesGUI.class);
        GUI = new DialoguesGUI();
    }

    private DialoguesGUI()
    {
    }

    public static void show(ArrayList<CDialogue> dialogues)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);

        dialogueLinks.clear();
        scrollView.clear();
        for (CDialogue dialogue : dialogues)
        {
            GUITextButton button = new GUITextButton(GUI, dialogue.name.value);
            scrollView.add(button);
            dialogueLinks.put(button, dialogue);
        }
    }

    @Override
    protected void init()
    {
        scrollView = new GUIScrollView(this, 0, 0, 0.98, 1);
        scrollView.setSubElementAutoplaceMethod(AP_CENTER_V_CENTER_H);
        guiElements.add(scrollView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
    }

    @SubscribeEvent
    public static void click(GUILeftClickEvent event)
    {
        CDialogue dialogue = dialogueLinks.get(event.getElement());
        if (dialogue != null) DialogueGUI.show(dialogue);
    }
}

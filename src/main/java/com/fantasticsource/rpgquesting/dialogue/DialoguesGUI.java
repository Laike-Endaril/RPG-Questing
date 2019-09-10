package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.guielements.rect.text.GUITextButton;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DialoguesGUI extends GUIScreen
{
    public static DialoguesGUI GUI;
    private static GUIRectScrollView scrollView;
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
        double yy = 0;
        for (CDialogue dialogue : dialogues)
        {
            GUITextButton button = new GUITextButton(GUI, 0, yy, dialogue.name.value);
            scrollView.add(button);
            dialogueLinks.put(button, dialogue);

            yy += button.height;
        }
    }

    @Override
    protected void init()
    {
        scrollView = new GUIRectScrollView(this, 0, 0, 0.98, 1);
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

package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class DialogueGUI extends GUIScreen
{
    public static DialogueGUI GUI;

    private static GUIScrollView scrollView;

    private static CDialogueBranch current = null;
    private static ArrayList<String> lines = new ArrayList<>();

    static
    {
        GUI = new DialogueGUI();
        MinecraftForge.EVENT_BUS.register(DialogueGUI.class);
    }

    private DialogueGUI()
    {
    }

    public static void show(DialogueBranchPacket packet)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);

        if (packet.clear) lines.clear();

        current = packet.branch;
        lines.add(current.paragraph.value);
        lines.add("\n");

        scrollView.clear();
        for (String line : lines) scrollView.add(new GUIText(GUI, processString(line)));
        for (CDialogueChoice choice : current.choices)
        {
            scrollView.add(new GUIText(GUI, processString(choice.text.value), Color.GREEN, Color.AQUA, Color.WHITE));
        }
    }

    public static String processString(String string)
    {
        return string.replaceAll("@p", Minecraft.getMinecraft().player.getName());
    }

    @Override
    protected void init()
    {
        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        scrollView = new GUIScrollView(this, 0, 0, 0.98, 1);
        guiElements.add(scrollView);
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView);
        guiElements.add(scrollbar);
    }

    @SubscribeEvent
    public static void click(GUILeftClickEvent event)
    {
        String s = event.getElement().toString();
        for (CDialogueChoice choice : current.choices)
        {
            if (choice.text.value.equals(s))
            {
                Network.WRAPPER.sendToServer(new Network.MakeChoicePacket(current, s));
                break;
            }
        }
    }
}

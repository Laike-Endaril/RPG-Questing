package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.ActionErrorPacket;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class DialogueGUI extends GUIScreen
{
    private static final Color C_CHOICE = Color.GREEN.copy().setRF(0.2f).setBF(0.2f);
    private static final TextFormatting
            TF_OLD_CHOICE = TextFormatting.DARK_GREEN,
            TF_OLD_DIALOGUE = TextFormatting.GRAY;

    public static DialogueGUI GUI;

    private static GUIScrollView scrollView;

    private static CDialogueBranch current = null;
    private static ArrayList<String> lines = new ArrayList<>();
    private static ArrayList<GUIText> errorLines = new ArrayList<>();

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
        clearErrors();

        current = packet.branch;
        lines.add(current.paragraph.value);
        lines.add("\n");


        scrollView.clear();

        for (String line : lines) scrollView.add(new GUIText(GUI, processString(line)));

        for (CDialogueChoice choice : current.choices)
        {
            scrollView.add(new GUIText(GUI, processString(choice.text.value) + '\n', C_CHOICE, Color.AQUA, Color.YELLOW));
        }
    }

    public static void showChoiceActionError(ActionErrorPacket packet)
    {
        clearErrors();
        if (packet.error.size() > 0)
        {
            GUIText text = new GUIText(GUI, "\n\nThe following conditions must be met:\n\n", Color.RED, Color.RED, Color.RED);

            scrollView.add(text);
            errorLines.add(text);

            ArrayList<String> lines = packet.error;
            if (lines.get(0).equals("ALL OF:"))
            {
                lines.remove(lines.size() - 1);
                lines.remove(0);
                lines.remove(0);

                for (int i = 0; i < lines.size(); i++)
                {
                    lines.set(i, lines.get(i).substring(1));
                }
            }
            for (String line : lines)
            {
                text = new GUIText(GUI, processString("* " + line + "\n"), Color.RED, Color.RED, Color.RED);
                scrollView.add(text);
                errorLines.add(text);
            }
        }
    }

    public static void clearErrors()
    {
        for (GUIText text : errorLines) scrollView.remove(text);
        errorLines.clear();
    }

    public static String processString(String string)
    {
        return string.replaceAll("@p|@P", Minecraft.getMinecraft().player.getName());
    }

    @SubscribeEvent
    public static void click(GUILeftClickEvent event)
    {
        if (event.getScreen() != GUI) return;

        GUIElement element = event.getElement();
        if (element.getClass() != GUIText.class) return;

        String s = event.getElement().toString();
        s = s.substring(0, s.length() - 1);
        for (CDialogueChoice choice : current.choices)
        {
            if (choice.text.value.equals(s))
            {
                for (int i = 0; i < lines.size(); i++)
                {
                    String line = lines.get(i);
                    if (!line.contains(TF_OLD_CHOICE.toString())) lines.set(i, TF_OLD_DIALOGUE + line);
                }
                lines.add(TF_OLD_CHOICE + s + "\n\n\n");
                Network.WRAPPER.sendToServer(new Network.MakeChoicePacket(current, s));
                break;
            }
        }
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
}

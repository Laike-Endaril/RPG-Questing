package com.fantasticsource.rpgquesting.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.rpgquesting.Colors;
import com.fantasticsource.rpgquesting.Network;
import com.fantasticsource.rpgquesting.Network.ActionErrorPacket;
import com.fantasticsource.rpgquesting.Network.DialogueBranchPacket;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class DialogueGUI extends GUIScreen
{
    public static final DialogueGUI GUI = new DialogueGUI();
    private static final Color C_CHOICE = Color.GREEN.copy().setRF(0.2f).setBF(0.2f);
    private static final TextFormatting
            TF_OLD_CHOICE = TextFormatting.DARK_GREEN,
            TF_OLD_DIALOGUE = TextFormatting.GRAY;
    private static GUIScrollView scrollView;

    private static ArrayList<String> lines = new ArrayList<>();
    private static ArrayList<GUIText> errorLines = new ArrayList<>();

    private DialogueGUI()
    {
    }

    public static void show(DialogueBranchPacket packet)
    {
        Minecraft.getMinecraft().displayGuiScreen(GUI);

        if (packet.clear) lines.clear();
        clearErrors();

        lines.add(packet.paragraph.value);
        lines.add("\n");


        scrollView.clear();

        for (String line : lines) scrollView.add(new GUIText(GUI, processString(line)));

        for (String choice : packet.choices)
        {
            GUIText choiceElement = new GUIText(GUI, processString(choice) + '\n', C_CHOICE, Color.AQUA, Color.YELLOW);
            scrollView.add(choiceElement.addClickActions(() ->
            {
                for (int i = 0; i < lines.size(); i++)
                {
                    String line = lines.get(i);
                    if (!line.contains(TF_OLD_DIALOGUE.toString()) && !line.contains(TF_OLD_CHOICE.toString())) lines.set(i, TF_OLD_DIALOGUE + line);
                }
                String s = choiceElement.toString();
                s = s.substring(0, s.length() - 1);
                lines.add(TF_OLD_CHOICE + s + "\n\n\n");
                Network.WRAPPER.sendToServer(new Network.MakeChoicePacket(s));
            }));
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

    @Override
    protected void init()
    {
        root.add(new GUIGradient(this, 0, 0, 1, 1, Colors.T_BLACK));

        scrollView = new GUIScrollView(this, 0, 0, 0.98, 1);
        root.add(scrollView);
        root.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));
    }
}

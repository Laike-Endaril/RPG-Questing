package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.rpgquesting.conditions.element.GUICondition;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class ConditionPickerGUI extends GUIScreen
{
    private GUICondition element;

    public static void show(GUICondition element)
    {
        ConditionPickerGUI gui = new ConditionPickerGUI();

        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(gui);
        else Minecraft.getMinecraft().displayGuiScreen(gui);

        gui.element = element;
    }

    @Override
    protected void init()
    {
        drawStack = false;

        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));
    }
}

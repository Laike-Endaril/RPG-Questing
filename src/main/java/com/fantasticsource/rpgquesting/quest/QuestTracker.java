package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.Render;
import com.fantasticsource.rpgquesting.gui.JournalGUI;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

import static com.fantasticsource.rpgquesting.Colors.*;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class QuestTracker
{
    public static String questname = "";
    public static ArrayList<CObjective> objectives = new ArrayList<>();
    public static int padding = 5;
    public static float backdropAlpha = 0.3f;

    public static void stopTracking(String name)
    {
        if (questname != null && questname.equals(name)) stopTracking();
    }

    public static void stopTracking()
    {
        startTracking("", new ArrayList<>());
    }

    public static void startTracking(String questName, ArrayList<CObjective> objectives)
    {
        QuestTracker.questname = questName;
        QuestTracker.objectives = objectives;

        JournalGUI.setQuestViewProgressMode(JournalGUI.viewedQuest);
    }

    @SubscribeEvent
    public static void hud(Render.RenderHUDEvent event)
    {
        if (questname == null || questname.equals("")) return;


        GlStateManager.pushMatrix();


        //Populate string list to render
        ArrayList<Color> colors = new ArrayList<>();
        ArrayList<String> elements = new ArrayList<>();
        boolean questDone = true, questStarted = false;
        for (CObjective objective : objectives)
        {
            if (objective.isDone())
            {
                questStarted = true;
                colors.add(GREEN[0]);
            }
            else if (objective.isStarted())
            {
                questStarted = true;
                questDone = false;
                colors.add(YELLOW[0]);
            }
            else
            {
                questDone = false;
                colors.add(RED[0]);
            }

            elements.add(objective.getFullText());
        }
        Color c = questDone ? GREEN[0] : questStarted ? YELLOW[0] : RED[0];
        colors.add(0, c);
        elements.add(0, questname);


        //Maths
        event.setScalingMode(Render.SCALING_MC_GUI);
        GlStateManager.scale(0.5f, 0.5f, 1);
        int screenWidth = event.getWidth() << 1, screenHeight = event.getHeight() << 1;

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        int boxWidth = 0, boxHeight = (padding << 1) + (fr.FONT_HEIGHT + padding) * elements.size();
        for (String element : elements) boxWidth = Tools.max(boxWidth, fr.getStringWidth(element));
        boxWidth += padding << 1;

        int x2 = screenWidth, x1 = x2 - boxWidth;
        int y1 = (screenHeight - boxHeight) >> 1, y2 = y1 + boxHeight;


        //Draw backdrop
        GlStateManager.disableTexture2D();
        GlStateManager.color(0, 0, 0, backdropAlpha);

        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glVertex3f(x1, y1, 0);
        GlStateManager.glVertex3f(x1, y2, 0);
        GlStateManager.glVertex3f(x2, y2, 0);
        GlStateManager.glVertex3f(x2, y1, 0);
        GlStateManager.glEnd();


        //Draw questname / objective separation line
        c = colors.get(0);
        int xx = x1 + padding, yy = y1 + padding + fr.FONT_HEIGHT + padding;

        GlStateManager.color(c.rf(), c.gf(), c.bf(), 1);
        GlStateManager.glBegin(GL_LINES);
        GlStateManager.glVertex3f(xx, yy - 1, 0);
        GlStateManager.glVertex3f(xx + boxWidth - (padding << 1), yy - 1, 0);
        GlStateManager.glEnd();


        //Draw text
        GlStateManager.enableTexture2D();

        fr.drawString(elements.get(0), x1 + ((boxWidth - fr.getStringWidth(elements.get(0))) >> 1), y1 + padding, c.color() >> 8);

        yy += padding;
        for (int i = 1; i < elements.size(); i++)
        {
            fr.drawString(elements.get(i), xx, yy, colors.get(i).color() >> 8);
            yy += fr.FONT_HEIGHT + padding;
        }


        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1, 1);
    }
}

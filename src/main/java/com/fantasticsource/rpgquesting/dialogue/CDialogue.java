package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CDialogue extends Component
{
    //Only exists server-side
    public CStringUTF8 saveName;
    public CStringUTF8 displayName; //Visible to the player on the dialogue selection screen (DialoguesGUI) when multiple dialogues are available
    public ArrayList<CDialogueBranch> branches = new ArrayList<>(); //All branches that make up the possible dialogue routes that can be taken
    public int currentBranch = 0; //The currently active branch (enforce changes upon packet received from client)
    public ArrayList<CDialogueFilter> filters = new ArrayList<>();

    public CDialogue(String saveName, String displayName)
    {
        this.saveName = new CStringUTF8().set(saveName);
        this.displayName = new CStringUTF8().set(displayName);
    }

    public boolean entityHas(Entity entity)
    {
        for (CDialogueFilter filter : filters)
        {
            if (filter.allowed(entity)) return true;
        }
        return false;
    }

    @Override
    public CDialogue write(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogue read(ByteBuf byteBuf)
    {
        return this;
    }

    @Override
    public CDialogue save(FileOutputStream fileOutputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogue load(FileInputStream fileInputStream) throws IOException
    {
        return this;
    }

    @Override
    public CDialogue parse(String s)
    {
        return this;
    }

    @Override
    public CDialogue copy()
    {
        return null;
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        return null;
    }

    @Override
    public CDialogue setFromGUIElement(GUIElement guiElement)
    {
        return this;
    }
}

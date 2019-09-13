package com.fantasticsource.rpgquesting.dialogue;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.mctools.component.CUUID;
import com.fantasticsource.mctools.component.Component;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class CDialogue extends Component
{
    CUUID sessionID = new CUUID().set(UUID.randomUUID());
    public CStringUTF8 saveName;
    public CStringUTF8 displayName;
    public ArrayList<CDialogueFilter> filters = new ArrayList<>();
    public ArrayList<CDialogueBranch> branches = new ArrayList<>();
    public int currentBranch = 0;

    public CDialogue(String saveName, String displayName)
    {
        this.saveName = new CStringUTF8().set(saveName);
        this.displayName = new CStringUTF8().set(displayName);
    }

    public CDialogue add(CDialogueFilter filter)
    {
        filters.add(filter);
        return this;
    }

    public CDialogue add(CDialogueBranch branch)
    {
        branches.add(branch.setParent(this));
        return this;
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

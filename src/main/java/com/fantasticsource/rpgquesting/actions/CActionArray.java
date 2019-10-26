package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUIAction;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class CActionArray extends CAction
{
    public ArrayList<CAction> actions = new ArrayList<>();

    public CActionArray()
    {
    }

    public CActionArray(CAction... actions)
    {
        add(actions);
    }

    public CActionArray add(CAction... actions)
    {
        this.actions.addAll(Arrays.asList(actions));
        return this;
    }

    @Override
    protected void execute(Entity entity)
    {
        for (CAction action : actions) action.execute(entity);
    }

    @Override
    public ArrayList<String> description()
    {
        ArrayList<String> result = new ArrayList<>();

        for (CAction action : actions)
        {
            result.addAll(action.description());
        }

        for (int i = 0; i < result.size(); i++)
        {
            result.set(i, " " + result.get(i));
        }

        result.add(0, "{");
        result.add("}");
        result.add(0, "ALL OF:");

        return result;
    }

    @Override
    public void updateRelations(String dialogueName, int type, int index)
    {
        for (CAction action : actions) action.updateRelations(dialogueName, type, index);
    }

    @Override
    public CActionArray write(ByteBuf buf)
    {
        super.write(buf);

        new CInt().set(actions.size()).write(buf);
        for (CAction action : actions) Component.writeMarked(buf, action);

        return this;
    }

    @Override
    public CActionArray read(ByteBuf buf)
    {
        super.read(buf);

        actions.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--) actions.add((CAction) Component.readMarked(buf));

        return this;
    }

    @Override
    public CActionArray save(OutputStream stream)
    {
        super.save(stream);

        new CInt().set(actions.size()).save(stream);
        for (CAction action : actions) Component.saveMarked(stream, action);

        return this;
    }

    @Override
    public CActionArray load(InputStream stream)
    {
        super.load(stream);

        actions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) actions.add((CAction) Component.loadMarked(stream));

        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GUIAction getChoosableElement(GUIScreen screen)
    {
        GUIAction element = new GUIAction(screen, new CActionArray());
        element.text = "Array (multiple actions)";
        return element;
    }
}

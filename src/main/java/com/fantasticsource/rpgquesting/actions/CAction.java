package com.fantasticsource.rpgquesting.actions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.conditions.CCondition;
import com.fantasticsource.rpgquesting.conditions.CConditionAnd;
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

public abstract class CAction extends Component
{
    public ArrayList<CCondition> conditions = new ArrayList<>();

    public CAction addConditions(CCondition... conditions)
    {
        this.conditions.addAll(Arrays.asList(conditions));
        return this;
    }

    public final ArrayList<String> tryExecute(Entity entity)
    {
        ArrayList<String> result = new CConditionAnd().add(conditions.toArray(new CCondition[0])).unmetConditions(entity);
        if (result.size() == 0) execute(entity);
        return result;
    }

    protected abstract void execute(Entity entity);

    public abstract ArrayList<String> description();

    public void updateRelations(String dialogueName, int type, int index)
    {
    }

    @SideOnly(Side.CLIENT)
    public abstract GUIAction getChoosableElement(GUIScreen screen);

    @Override
    public CAction write(ByteBuf buf)
    {
        new CInt().set(conditions.size()).write(buf);
        for (CCondition condition : conditions) Component.writeMarked(buf, condition);

        return this;
    }

    @Override
    public CAction read(ByteBuf buf)
    {
        conditions.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--) conditions.add((CCondition) Component.readMarked(buf));

        return this;
    }

    @Override
    public CAction save(OutputStream stream)
    {
        new CInt().set(conditions.size()).save(stream);
        for (CCondition condition : conditions) Component.saveMarked(stream, condition);

        return this;
    }

    @Override
    public CAction load(InputStream stream)
    {
        conditions.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) conditions.add((CCondition) Component.loadMarked(stream));

        return this;
    }
}

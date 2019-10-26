package com.fantasticsource.rpgquesting.conditions;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.rpgquesting.gui.GUICondition;
import com.fantasticsource.tools.component.Component;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public abstract class CCondition extends Component
{
    public abstract ArrayList<String> unmetConditions(Entity entity);

    public abstract ArrayList<String> description();

    @SideOnly(Side.CLIENT)
    public abstract GUICondition getChoosableElement(GUIScreen screen);

    public void updateRelations(String dialogueName, int type, int index)
    {
    }
}

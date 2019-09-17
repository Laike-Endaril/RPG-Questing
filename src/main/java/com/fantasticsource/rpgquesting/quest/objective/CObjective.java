package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class CObjective extends Component
{
    CUUID owner = new CUUID();
    CStringUTF8 text = new CStringUTF8();
    CBoolean progressIsPrefix = new CBoolean().set(true);

    public final CObjective setOwner(EntityPlayerMP player)
    {
        owner.set(player.getPersistentID());
        return this;
    }

    public final String getFullText()
    {
        if (progressIsPrefix.value) return progressText() + " " + text;

        return text + " " + progressText();
    }

    protected abstract String progressText();

    public abstract boolean isStarted();

    public abstract boolean isDone();
}

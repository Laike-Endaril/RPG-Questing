package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;

public abstract class CObjective extends Component
{
    CStringUTF8 text = new CStringUTF8();

    public final String getFullText()
    {
        return getPrefix() + " " + text;
    }

    protected abstract String getPrefix();
}

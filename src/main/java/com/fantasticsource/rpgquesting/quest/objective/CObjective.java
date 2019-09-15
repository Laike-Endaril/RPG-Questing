package com.fantasticsource.rpgquesting.quest.objective;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;

public abstract class CObjective extends Component
{
    CUUID owner = new CUUID();
    CStringUTF8 text = new CStringUTF8();
    CBoolean progressIsPrefix = new CBoolean().set(true);

    public final String getFullText()
    {
        if (progressIsPrefix.value) return progressText() + " " + text;

        return text + " " + progressText();
    }

    protected abstract String progressText();
}

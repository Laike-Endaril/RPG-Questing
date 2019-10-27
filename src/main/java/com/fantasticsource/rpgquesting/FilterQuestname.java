package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;

public class FilterQuestname extends TextFilter<String>
{
    public static final FilterQuestname INSTANCE = new FilterQuestname();

    private FilterQuestname()
    {
    }

    @Override
    public String transformInput(String s)
    {
        return s;
    }

    @Override
    public boolean acceptable(String s)
    {
        return FilterNotEmpty.INSTANCE.acceptable(s) && !s.contains("_");
    }

    @Override
    public String parse(String s)
    {
        return s;
    }
}

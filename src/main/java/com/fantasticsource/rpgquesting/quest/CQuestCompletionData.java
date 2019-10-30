package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.MCTimestamp;
import com.fantasticsource.mctools.component.CMCTimestamp;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CQuestCompletionData extends Component
{
    public CStringUTF8 questName = new CStringUTF8();
    public CMCTimestamp timestamp = new CMCTimestamp();

    public CQuestCompletionData()
    {
    }

    public CQuestCompletionData(String questName, MCTimestamp timestamp)
    {
        set(questName, timestamp);
    }

    public CQuestCompletionData set(String questName, MCTimestamp timestamp)
    {
        this.questName.set(questName);
        this.timestamp.set(timestamp);

        return this;
    }

    @Override
    public CQuestCompletionData write(ByteBuf buf)
    {
        questName.write(buf);
        timestamp.write(buf);

        return this;
    }

    @Override
    public CQuestCompletionData read(ByteBuf buf)
    {
        questName.read(buf);
        timestamp.read(buf);

        return this;
    }

    @Override
    public CQuestCompletionData save(OutputStream stream)
    {
        questName.save(stream);
        timestamp.save(stream);

        return this;
    }

    @Override
    public CQuestCompletionData load(InputStream stream)
    {
        questName.load(stream);
        timestamp.load(stream);

        return this;
    }
}

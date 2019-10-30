package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.MCTimestamp;
import com.fantasticsource.mctools.component.CMCTimestamp;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

import java.io.InputStream;
import java.io.OutputStream;

public class CQuestTimestamp extends Component
{
    public CStringUTF8 questName = new CStringUTF8();
    public CMCTimestamp timestamp = new CMCTimestamp();

    public CQuestTimestamp()
    {
    }

    public CQuestTimestamp(String questName, World world)
    {
        set(questName, new MCTimestamp(world));
    }

    public CQuestTimestamp(String questName, MCTimestamp timestamp)
    {
        set(questName, timestamp);
    }

    public CQuestTimestamp set(String questName, MCTimestamp timestamp)
    {
        this.questName.set(questName);
        this.timestamp.set(timestamp);

        return this;
    }

    @Override
    public CQuestTimestamp write(ByteBuf buf)
    {
        questName.write(buf);
        timestamp.write(buf);

        return this;
    }

    @Override
    public CQuestTimestamp read(ByteBuf buf)
    {
        questName.read(buf);
        timestamp.read(buf);

        return this;
    }

    @Override
    public CQuestTimestamp save(OutputStream stream)
    {
        questName.save(stream);
        timestamp.save(stream);

        return this;
    }

    @Override
    public CQuestTimestamp load(InputStream stream)
    {
        questName.load(stream);
        timestamp.load(stream);

        return this;
    }
}

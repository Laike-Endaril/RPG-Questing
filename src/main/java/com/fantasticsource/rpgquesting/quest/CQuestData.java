package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class CQuestData extends Component
{
    public UUID player = null;
    public ArrayList<UUID> completedQuests = new ArrayList<>();
    public ArrayList<UUID> inProgressQuests = new ArrayList<>();

    @Override
    public Component write(ByteBuf buf)
    {
        buf.writeInt(completedQuests.size());
        for (UUID id : completedQuests) new CUUID().set(id).write(buf);
        buf.writeInt(inProgressQuests.size());
        for (UUID id : inProgressQuests) new CUUID().set(id).write(buf);
        return this;
    }

    @Override
    public Component read(ByteBuf buf)
    {
        for (int i = new CInt().read(buf).value; i > 0; i--) completedQuests.add(new CUUID().read(buf).value);
        for (int i = new CInt().read(buf).value; i > 0; i--) inProgressQuests.add(new CUUID().read(buf).value);
        return this;
    }

    @Override
    public Component save(OutputStream stream) throws IOException
    {
        new CInt().set(completedQuests.size()).save(stream);
        for (UUID id : completedQuests) new CUUID().set(id).save(stream);
        new CInt().set(inProgressQuests.size()).save(stream);
        for (UUID id : inProgressQuests) new CUUID().set(id).save(stream);
        return this;
    }

    @Override
    public Component load(InputStream stream) throws IOException
    {
        for (int i = new CInt().load(stream).value; i > 0; i--) completedQuests.add(new CUUID().load(stream).value);
        for (int i = new CInt().load(stream).value; i > 0; i--) inProgressQuests.add(new CUUID().load(stream).value);
        return this;
    }
}

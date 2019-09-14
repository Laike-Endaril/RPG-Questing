package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CQuestData extends Component
{
    public CUUID player = new CUUID();
    public ArrayList<CUUID> completedQuests = new ArrayList<>();
    public ArrayList<CUUID> inProgressQuests = new ArrayList<>();

    @Override
    public Component write(ByteBuf buf)
    {
        buf.writeInt(completedQuests.size());
        for (CUUID id : completedQuests) id.write(buf);
        buf.writeInt(inProgressQuests.size());
        for (CUUID id : inProgressQuests) id.write(buf);
        return this;
    }

    @Override
    public Component read(ByteBuf buf)
    {
        for (int i = new CInt().read(buf).value; i > 0; i--) completedQuests.add(new CUUID().read(buf));
        for (int i = new CInt().read(buf).value; i > 0; i--) inProgressQuests.add(new CUUID().read(buf));
        return this;
    }

    @Override
    public Component save(OutputStream stream) throws IOException
    {
        new CInt().set(completedQuests.size()).save(stream);
        for (CUUID id : completedQuests) id.save(stream);
        new CInt().set(inProgressQuests.size()).save(stream);
        for (CUUID id : inProgressQuests) id.save(stream);
        return this;
    }

    @Override
    public Component load(InputStream stream) throws IOException
    {
        for (int i = new CInt().load(stream).value; i > 0; i--) completedQuests.add(new CUUID().load(stream));
        for (int i = new CInt().load(stream).value; i > 0; i--) inProgressQuests.add(new CUUID().load(stream));
        return this;
    }
}

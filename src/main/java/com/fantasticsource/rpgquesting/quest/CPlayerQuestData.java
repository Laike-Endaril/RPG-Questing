package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class CPlayerQuestData extends Component
{
    public final EntityPlayerMP player;
    public ArrayList<UUID> completedQuests = new ArrayList<>();
    public LinkedHashMap<UUID, ArrayList<CObjective>> inProgressQuests = new LinkedHashMap<>();

    public CPlayerQuestData(EntityPlayerMP player)
    {
        this.player = player;
    }

    public CPlayerQuestData save()
    {
        File file = RPGQuesting.playerDataFolder;
        if (!file.exists()) file.mkdir();

        file = new File(file.getAbsolutePath() + File.separator + RPGQuesting.MODID);
        if (!file.exists()) file.mkdir();

        file = new File(file.getAbsolutePath() + File.separator + player.getPersistentID() + ".dat");
        File file2 = new File(file.getAbsolutePath() + ".new");

        if (file2.exists()) file2.delete();
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file2);
            save(fos);
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (file.exists()) file.delete();
        file2.renameTo(file);

        return this;
    }

    public CPlayerQuestData load() throws IOException
    {
        File file = new File(RPGQuesting.playerDataFolder.getAbsolutePath() + File.separator + RPGQuesting.MODID + File.separator + player.getPersistentID() + ".dat");
        if (!file.exists()) return null;

        FileInputStream fis = new FileInputStream(file);
        load(fis);
        fis.close();

        return this;
    }

    @Override
    public CPlayerQuestData write(ByteBuf buf)
    {
        buf.writeInt(completedQuests.size());
        for (UUID id : completedQuests) new CUUID().set(id).write(buf);

        buf.writeInt(inProgressQuests.size());
        for (Map.Entry<UUID, ArrayList<CObjective>> entry : inProgressQuests.entrySet())
        {
            new CUUID().set(entry.getKey()).write(buf);

            ArrayList<CObjective> objectives = entry.getValue();
            buf.writeInt(objectives.size());
            for (CObjective objective : objectives) Component.writeMarked(buf, objective);
        }

        return this;
    }

    @Override
    public CPlayerQuestData read(ByteBuf buf)
    {
        completedQuests.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--) completedQuests.add(new CUUID().read(buf).value);

        inProgressQuests.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--)
        {
            ArrayList<CObjective> objectives = new ArrayList<>();
            inProgressQuests.put(new CUUID().read(buf).value, objectives);

            for (int i2 = new CInt().read(buf).value; i2 > 0; i2--)
            {
                objectives.add((CObjective) Component.readMarked(buf));
            }
        }

        return this;
    }

    @Override
    public CPlayerQuestData save(OutputStream stream) throws IOException
    {
        new CInt().set(completedQuests.size()).save(stream);
        for (UUID id : completedQuests) new CUUID().set(id).save(stream);

        new CInt().set(inProgressQuests.size()).save(stream);
        for (Map.Entry<UUID, ArrayList<CObjective>> entry : inProgressQuests.entrySet())
        {
            new CUUID().set(entry.getKey()).save(stream);

            ArrayList<CObjective> objectives = entry.getValue();
            new CInt().set(objectives.size()).save(stream);
            for (CObjective objective : objectives) Component.saveMarked(stream, objective);
        }

        return this;
    }

    @Override
    public CPlayerQuestData load(InputStream stream) throws IOException
    {
        completedQuests.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) completedQuests.add(new CUUID().load(stream).value);

        inProgressQuests.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--)
        {
            ArrayList<CObjective> objectives = new ArrayList<>();
            inProgressQuests.put(new CUUID().load(stream).value, objectives);

            for (int i2 = new CInt().load(stream).value; i2 > 0; i2--)
            {
                objectives.add((CObjective) Component.loadMarked(stream));
            }
        }

        completedQuests.removeIf(e -> CQuests.get(e) == null);
        inProgressQuests.entrySet().removeIf(e -> CQuests.get(e.getKey()) == null);

        return this;
    }
}

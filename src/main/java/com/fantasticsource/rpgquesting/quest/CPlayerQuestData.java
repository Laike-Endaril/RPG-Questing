package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CPlayerQuestData extends Component
{
    public EntityPlayerMP player;
    public CStringUTF8 trackedQuest = new CStringUTF8().set("");
    public LinkedHashMap<String, ArrayList<String>> completedQuests = new LinkedHashMap<>();
    public LinkedHashMap<String, LinkedHashMap<String, ArrayList<CObjective>>> inProgressQuests = new LinkedHashMap<>();


    public CPlayerQuestData()
    {
    }

    public CPlayerQuestData(EntityPlayerMP player)
    {
        this.player = player;
    }

    public CPlayerQuestData saveAndSync()
    {
        //Clear tracked quest if invalid
        CQuest q = CQuests.get(trackedQuest.value);
        if (q == null) trackedQuest.set("");
        else
        {
            LinkedHashMap<String, ArrayList<CObjective>> group = inProgressQuests.get(q.group.value);
            if (group == null || group.get(trackedQuest.value) == null) trackedQuest.set("");
        }


        //Save
        File file = RPGQuesting.playerDataFolder;
        if (!file.exists()) file.mkdir();

        file = new File(file.getAbsolutePath() + File.separator + RPGQuesting.MODID);
        if (!file.exists()) file.mkdir();

        file = new File(file.getAbsolutePath() + File.separator + player.getPersistentID() + ".dat");
        File file2 = new File(file.getAbsolutePath() + ".new");

        if (file2.exists()) file2.delete();
        FileOutputStream fos;
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


        //Sync
        if (player.world.playerEntities.contains(player))
        {
            CQuests.syncJournal(player);
            CQuests.syncTracker(player);
        }


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
        trackedQuest.write(buf);

        buf.writeInt(completedQuests.size());
        for (Map.Entry<String, ArrayList<String>> entry : completedQuests.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());

            ArrayList<String> groupQuests = entry.getValue();
            buf.writeInt(groupQuests.size());

            for (String name : groupQuests) new CStringUTF8().set(name).write(buf);
        }

        buf.writeInt(inProgressQuests.size());
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<CObjective>>> entry : inProgressQuests.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());

            LinkedHashMap<String, ArrayList<CObjective>> subMap = entry.getValue();
            buf.writeInt(subMap.size());

            for (Map.Entry<String, ArrayList<CObjective>> subEntry : subMap.entrySet())
            {
                new CStringUTF8().set(subEntry.getKey()).write(buf);

                ArrayList<CObjective> objectives = subEntry.getValue();
                buf.writeInt(objectives.size());

                for (CObjective objective : objectives) Component.writeMarked(buf, objective);
            }
        }

        return this;
    }

    @Override
    public CPlayerQuestData read(ByteBuf buf)
    {
        trackedQuest.read(buf);

        completedQuests.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            ArrayList<String> list = new ArrayList<>();
            completedQuests.put(ByteBufUtils.readUTF8String(buf), list);

            for (int i2 = buf.readInt(); i2 > 0; i2--)
            {
                list.add(new CStringUTF8().read(buf).value);
            }
        }

        inProgressQuests.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            LinkedHashMap<String, ArrayList<CObjective>> map = new LinkedHashMap<>();
            inProgressQuests.put(ByteBufUtils.readUTF8String(buf), map);

            for (int i2 = buf.readInt(); i2 > 0; i2--)
            {
                ArrayList<CObjective> objectives = new ArrayList<>();
                map.put(ByteBufUtils.readUTF8String(buf), objectives);

                for (int i3 = buf.readInt(); i3 > 0; i3--)
                {
                    objectives.add((CObjective) Component.readMarked(buf));
                }
            }
        }

        return this;
    }

    @Override
    public CPlayerQuestData save(OutputStream stream) throws IOException
    {
        trackedQuest.save(stream);

        new CInt().set(completedQuests.size()).save(stream);
        for (Map.Entry<String, ArrayList<String>> entry : completedQuests.entrySet())
        {
            new CStringUTF8().set(entry.getKey()).save(stream);

            ArrayList<String> groupQuests = entry.getValue();
            new CInt().set(groupQuests.size()).save(stream);

            for (String name : groupQuests) new CStringUTF8().set(name).save(stream);
        }

        new CInt().set(inProgressQuests.size()).save(stream);
        for (Map.Entry<String, LinkedHashMap<String, ArrayList<CObjective>>> entry : inProgressQuests.entrySet())
        {
            new CStringUTF8().set(entry.getKey()).save(stream);

            LinkedHashMap<String, ArrayList<CObjective>> subMap = entry.getValue();
            new CInt().set(subMap.size()).save(stream);

            for (Map.Entry<String, ArrayList<CObjective>> subEntry : subMap.entrySet())
            {
                new CStringUTF8().set(subEntry.getKey()).save(stream);

                ArrayList<CObjective> objectives = subEntry.getValue();
                new CInt().set(objectives.size()).save(stream);

                for (CObjective objective : objectives) Component.saveMarked(stream, objective);
            }
        }

        return this;
    }

    @Override
    public CPlayerQuestData load(InputStream stream) throws IOException
    {
        trackedQuest.load(stream);

        completedQuests.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--)
        {
            ArrayList<String> list = new ArrayList<>();
            completedQuests.put(new CStringUTF8().load(stream).value, list);

            for (int i2 = new CInt().load(stream).value; i2 > 0; i2--)
            {
                list.add(new CStringUTF8().load(stream).value);
            }
        }

        inProgressQuests.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--)
        {
            LinkedHashMap<String, ArrayList<CObjective>> map = new LinkedHashMap<>();
            inProgressQuests.put(new CStringUTF8().load(stream).value, map);

            for (int i2 = new CInt().load(stream).value; i2 > 0; i2--)
            {
                ArrayList<CObjective> objectives = new ArrayList<>();
                map.put(new CStringUTF8().load(stream).value, objectives);

                for (int i3 = new CInt().load(stream).value; i3 > 0; i3--)
                {
                    objectives.add((CObjective) Component.loadMarked(stream));
                }
            }
        }

        return this;
    }
}

package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.component.*;
import com.fantasticsource.tools.datastructures.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class CPlayerQuestData extends Component implements IObfuscatedComponent
{
    public EntityPlayerMP player;
    public CStringUTF8 trackedQuestName = new CStringUTF8().set("");
    public LinkedHashMap<String, ArrayList<String>> completedQuests = new LinkedHashMap<>();
    public LinkedHashMap<String, CQuestCompletionData> completionData = new LinkedHashMap<>();
    public LinkedHashMap<String, LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>>> inProgressQuests = new LinkedHashMap<>();


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
        CQuest q = CQuests.get(trackedQuestName.value);
        if (q == null) trackedQuestName.set("");
        else
        {
            LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> group = inProgressQuests.get(q.group.value);
            if (group == null || group.get(trackedQuestName.value) == null) trackedQuestName.set("");
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
            CQuests.syncJournal(player, "", false);
            CQuests.syncTracker(player);
        }


        return this;
    }

    public CPlayerQuestData load() throws IOException
    {
        File file = new File(RPGQuesting.playerDataFolder.getAbsolutePath() + File.separator + RPGQuesting.MODID + File.separator + player.getPersistentID() + ".dat");
        if (!file.exists()) return this;

        FileInputStream fis = new FileInputStream(file);
        load(fis);
        fis.close();

        for (Map.Entry<String, LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>>> groupEntry : inProgressQuests.entrySet())
        {
            String groupName = groupEntry.getKey();
            LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> groupQuests = groupEntry.getValue();

            for (Map.Entry<String, Pair<CUUID, ArrayList<CObjective>>> questEntry : groupQuests.entrySet())
            {
                String questName = questEntry.getKey();
                CQuest quest = CQuests.get(questName);
                if (quest == null || !quest.permanentID.value.equals(questEntry.getValue().getKey().value))
                {
                    groupQuests.remove(questName);
                    if (groupQuests.size() == 0) inProgressQuests.remove(groupName);
                }
            }
        }

        return this;
    }

    @Override
    public CPlayerQuestData write(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CPlayerQuestData read(ByteBuf buf)
    {
        return this;
    }

    @Override
    public CPlayerQuestData save(OutputStream stream)
    {
        trackedQuestName.save(stream);


        new CInt().set(completedQuests.size()).save(stream);
        for (Map.Entry<String, ArrayList<String>> entry : completedQuests.entrySet())
        {
            new CStringUTF8().set(entry.getKey()).save(stream);

            ArrayList<String> groupQuests = entry.getValue();
            new CInt().set(groupQuests.size()).save(stream);

            for (String name : groupQuests) new CStringUTF8().set(name).save(stream);
        }

        new CInt().set(completionData.size()).save(stream);
        for (Map.Entry<String, CQuestCompletionData> entry : completionData.entrySet())
        {
            new CStringUTF8().set(entry.getKey()).save(stream);
            entry.getValue().save(stream);
        }


        new CInt().set(inProgressQuests.size()).save(stream);
        for (Map.Entry<String, LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>>> entry : inProgressQuests.entrySet())
        {
            new CStringUTF8().set(entry.getKey()).save(stream);

            LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> subMap = entry.getValue();
            new CInt().set(subMap.size()).save(stream);

            for (Map.Entry<String, Pair<CUUID, ArrayList<CObjective>>> subEntry : subMap.entrySet())
            {
                new CStringUTF8().set(subEntry.getKey()).save(stream);

                subEntry.getValue().getKey().save(stream);

                ArrayList<CObjective> objectives = subEntry.getValue().getValue();
                new CInt().set(objectives.size()).save(stream);

                for (CObjective objective : objectives) Component.saveMarked(stream, objective);
            }
        }


        return this;
    }

    @Override
    public CPlayerQuestData load(InputStream stream)
    {
        trackedQuestName.load(stream);


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

        completionData.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--)
        {
            completionData.put(new CStringUTF8().load(stream).value, new CQuestCompletionData().load(stream));
        }


        inProgressQuests.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--)
        {
            LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> map = new LinkedHashMap<>();
            inProgressQuests.put(new CStringUTF8().load(stream).value, map);

            for (int i2 = new CInt().load(stream).value; i2 > 0; i2--)
            {
                ArrayList<CObjective> objectives = new ArrayList<>();

                map.put(new CStringUTF8().load(stream).value, new Pair<>(new CUUID().load(stream), objectives));

                for (int i3 = new CInt().load(stream).value; i3 > 0; i3--)
                {
                    objectives.add((CObjective) Component.loadMarked(stream));
                }
            }
        }


        return this;
    }

    @Override
    public Component writeObf(ByteBuf buf)
    {
        trackedQuestName.write(buf);


        buf.writeInt(completedQuests.size());
        for (Map.Entry<String, ArrayList<String>> entry : completedQuests.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());

            ArrayList<String> groupQuests = entry.getValue();
            buf.writeInt(groupQuests.size());

            for (String name : groupQuests) new CStringUTF8().set(name).write(buf);
        }

        new CInt().set(completionData.size()).write(buf);
        for (Map.Entry<String, CQuestCompletionData> entry : completionData.entrySet())
        {
            new CStringUTF8().set(entry.getKey()).write(buf);
            entry.getValue().write(buf);
        }


        buf.writeInt(inProgressQuests.size());
        for (Map.Entry<String, LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>>> entry : inProgressQuests.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());

            LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> subMap = entry.getValue();
            buf.writeInt(subMap.size());

            for (Map.Entry<String, Pair<CUUID, ArrayList<CObjective>>> subEntry : subMap.entrySet())
            {
                new CStringUTF8().set(subEntry.getKey()).write(buf);

                ArrayList<CObjective> objectives = subEntry.getValue().getValue();
                buf.writeInt(objectives.size());

                for (CObjective objective : objectives) Component.writeMarked(buf, objective);
            }
        }


        return this;
    }

    @Override
    public Component readObf(ByteBuf buf)
    {
        trackedQuestName.read(buf);


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

        completionData.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--)
        {
            completionData.put(new CStringUTF8().read(buf).value, new CQuestCompletionData().read(buf));
        }


        inProgressQuests.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            LinkedHashMap<String, Pair<CUUID, ArrayList<CObjective>>> map = new LinkedHashMap<>();
            inProgressQuests.put(ByteBufUtils.readUTF8String(buf), map);

            for (int i2 = buf.readInt(); i2 > 0; i2--)
            {
                ArrayList<CObjective> objectives = new ArrayList<>();
                map.put(ByteBufUtils.readUTF8String(buf), new Pair<>(new CUUID().set(UUID.randomUUID()), objectives));

                for (int i3 = buf.readInt(); i3 > 0; i3--)
                {
                    objectives.add((CObjective) Component.readMarked(buf));
                }
            }
        }


        return this;
    }
}

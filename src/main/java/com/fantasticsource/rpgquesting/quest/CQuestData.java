package com.fantasticsource.rpgquesting.quest;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.rpgquesting.RPGQuesting;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class CQuestData extends Component
{
    public final EntityPlayerMP player;
    public ArrayList<UUID> completedQuests = new ArrayList<>();
    public ArrayList<UUID> inProgressQuests = new ArrayList<>();

    public CQuestData(EntityPlayerMP player) throws IOException
    {
        this.player = player;
        load();
    }

    public CQuestData save() throws IOException
    {
        File file = new File(MCTools.getPlayerDataDir(FMLCommonHandler.instance().getMinecraftServerInstance()) + player.getPersistentID() + "." + RPGQuesting.MODID);
        File file2 = new File(MCTools.getPlayerDataDir(FMLCommonHandler.instance().getMinecraftServerInstance()) + player.getPersistentID() + "." + RPGQuesting.MODID + ".new");

        if (file2.exists()) file2.delete();
        FileOutputStream fos = new FileOutputStream(file2);
        save(fos);
        fos.close();

        if (file.exists()) file.delete();
        file2.renameTo(file);

        return this;
    }

    public CQuestData load() throws IOException
    {
        File file = new File(MCTools.getPlayerDataDir(FMLCommonHandler.instance().getMinecraftServerInstance()) + player.getPersistentID() + "." + RPGQuesting.MODID);
        if (!file.exists()) return null;

        FileInputStream fis = new FileInputStream(file);
        load(fis);
        fis.close();

        return this;
    }

    @Override
    public CQuestData write(ByteBuf buf)
    {
        buf.writeInt(completedQuests.size());
        for (UUID id : completedQuests) new CUUID().set(id).write(buf);
        buf.writeInt(inProgressQuests.size());
        for (UUID id : inProgressQuests) new CUUID().set(id).write(buf);

        return this;
    }

    @Override
    public CQuestData read(ByteBuf buf)
    {
        for (int i = new CInt().read(buf).value; i > 0; i--) completedQuests.add(new CUUID().read(buf).value);
        for (int i = new CInt().read(buf).value; i > 0; i--) inProgressQuests.add(new CUUID().read(buf).value);

        return this;
    }

    @Override
    public CQuestData save(OutputStream stream) throws IOException
    {
        new CInt().set(completedQuests.size()).save(stream);
        for (UUID id : completedQuests) new CUUID().set(id).save(stream);
        new CInt().set(inProgressQuests.size()).save(stream);
        for (UUID id : inProgressQuests) new CUUID().set(id).save(stream);

        return this;
    }

    @Override
    public CQuestData load(InputStream stream) throws IOException
    {
        for (int i = new CInt().load(stream).value; i > 0; i--) completedQuests.add(new CUUID().load(stream).value);
        for (int i = new CInt().load(stream).value; i > 0; i--) inProgressQuests.add(new CUUID().load(stream).value);

        return this;
    }
}

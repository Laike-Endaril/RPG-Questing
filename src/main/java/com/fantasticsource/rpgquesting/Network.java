package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.component.CStringUTF8;
import com.fantasticsource.rpgquesting.dialogue.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(RPGQuesting.MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(DialogueBranchPacketHandler.class, DialogueBranchPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(MultipleDialoguesPacketHandler.class, MultipleDialoguesPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(ChooseDialoguePacketHandler.class, ChooseDialoguePacket.class, discriminator++, Side.SERVER);
    }


    public static class DialogueBranchPacket implements IMessage
    {
        public boolean clear;
        public CDialogueBranch branch;

        public DialogueBranchPacket()
        {
            //Required
        }

        public DialogueBranchPacket(boolean clear, CDialogueBranch branch)
        {
            this.clear = clear;
            this.branch = branch;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(clear);
            branch.writeObf(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            clear = buf.readBoolean();
            branch = new CDialogueBranch().readObf(buf);
        }
    }

    public static class DialogueBranchPacketHandler implements IMessageHandler<DialogueBranchPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(DialogueBranchPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> DialogueGUI.show(packet));
            return null;
        }
    }


    public static class MultipleDialoguesPacket implements IMessage
    {
        public int targetID;
        public ArrayList<CStringUTF8> dialogueSaveNames = new ArrayList<>();
        public ArrayList<CStringUTF8> dialogueDisplayNames = new ArrayList<>();

        public MultipleDialoguesPacket()
        {
            //Required
        }

        public MultipleDialoguesPacket(int targetID, ArrayList<CDialogue> dialogues)
        {
            this.targetID = targetID;
            for (CDialogue dialogue : dialogues)
            {
                dialogueSaveNames.add(dialogue.saveName);
                dialogueDisplayNames.add(dialogue.displayName);
            }
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(targetID);
            buf.writeInt(dialogueSaveNames.size());
            for (CStringUTF8 dialogueSaveName : dialogueSaveNames) dialogueSaveName.write(buf);
            for (CStringUTF8 dialogueDisplayName : dialogueDisplayNames) dialogueDisplayName.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            targetID = buf.readInt();
            int size = buf.readInt();
            for (int i = size; i > 0; i--)
            {
                dialogueSaveNames.add(new CStringUTF8().read(buf));
            }
            for (int i = size; i > 0; i--)
            {
                dialogueDisplayNames.add(new CStringUTF8().read(buf));
            }
        }
    }

    public static class MultipleDialoguesPacketHandler implements IMessageHandler<MultipleDialoguesPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MultipleDialoguesPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> DialoguesGUI.show(packet));
            return null;
        }
    }


    public static class ChooseDialoguePacket implements IMessage
    {
        public int targetID;
        CStringUTF8 dialogueSavename;

        public ChooseDialoguePacket()
        {
            //Required
        }

        public ChooseDialoguePacket(int targetID, CStringUTF8 dialogueSaveName)
        {
            this.targetID = targetID;
            this.dialogueSavename = dialogueSaveName;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(targetID);
            dialogueSavename.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            targetID = buf.readInt();
            dialogueSavename = new CStringUTF8().read(buf);
        }
    }

    public static class ChooseDialoguePacketHandler implements IMessageHandler<ChooseDialoguePacket, IMessage>
    {
        @Override
        public IMessage onMessage(ChooseDialoguePacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                Entity target = player.world.getEntityByID(packet.targetID);
                if (target != null && target.getDistanceSq(player) < 25)
                {
                    WRAPPER.sendTo(new DialogueBranchPacket(true, Dialogues.get(packet.dialogueSavename.value).branches.get(0)), player);
                }
            });
            return null;
        }
    }
}

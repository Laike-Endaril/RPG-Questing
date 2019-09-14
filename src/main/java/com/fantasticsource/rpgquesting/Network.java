package com.fantasticsource.rpgquesting;

import com.fantasticsource.rpgquesting.dialogue.*;
import com.fantasticsource.tools.component.CStringUTF8;
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
        WRAPPER.registerMessage(CloseDialoguePacketHandler.class, CloseDialoguePacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(MakeChoicePacketHandler.class, MakeChoicePacket.class, discriminator++, Side.SERVER);
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
        public ArrayList<CStringUTF8> dialogueSaveNames = new ArrayList<>();
        public ArrayList<CStringUTF8> dialogueDisplayNames = new ArrayList<>();

        public MultipleDialoguesPacket()
        {
            //Required
        }

        public MultipleDialoguesPacket(ArrayList<CDialogue> dialogues)
        {
            for (CDialogue dialogue : dialogues)
            {
                dialogueSaveNames.add(dialogue.saveName);
                dialogueDisplayNames.add(dialogue.name);
            }
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(dialogueSaveNames.size());
            for (CStringUTF8 dialogueSaveName : dialogueSaveNames) dialogueSaveName.write(buf);
            for (CStringUTF8 dialogueDisplayName : dialogueDisplayNames) dialogueDisplayName.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
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
        int targetID;
        CStringUTF8 dialogueSavename;

        public ChooseDialoguePacket()
        {
            //Required
        }

        public ChooseDialoguePacket(CStringUTF8 dialogueSaveName)
        {
            this.dialogueSavename = dialogueSaveName;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(CDialogues.targetID);
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
                CDialogue dialogue = CDialogues.getBySessionID(packet.dialogueSavename.value);
                if (target != null && target.getDistanceSq(player) < 25 && dialogue.entityHas(target))
                {
                    WRAPPER.sendTo(new DialogueBranchPacket(true, dialogue.branches.get(0)), player);
                }
            });
            return null;
        }
    }


    public static class CloseDialoguePacket implements IMessage
    {
        @Override
        public void toBytes(ByteBuf buf)
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
        }
    }

    public static class CloseDialoguePacketHandler implements IMessageHandler<CloseDialoguePacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(CloseDialoguePacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> DialogueGUI.GUI.close());
            return null;
        }
    }


    public static class MakeChoicePacket implements IMessage
    {
        public CDialogueBranch currentBranch = new CDialogueBranch();
        public CStringUTF8 choice = new CStringUTF8();
        int targetID;

        public MakeChoicePacket()
        {
            //Required
        }

        public MakeChoicePacket(CDialogueBranch currentBranch, String choice)
        {
            this.choice.set(choice);
            this.currentBranch = currentBranch;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(CDialogues.targetID);
            currentBranch.writeObf(buf);
            choice.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            targetID = buf.readInt();
            currentBranch = currentBranch.readObf(buf);
            choice.read(buf);
        }
    }

    public static class MakeChoicePacketHandler implements IMessageHandler<MakeChoicePacket, IMessage>
    {
        @Override
        public IMessage onMessage(MakeChoicePacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                Entity target = player.world.getEntityByID(packet.targetID);
                CDialogue dialogue = CDialogues.getBySessionID(packet.currentBranch.parentID.value);
                if (target != null && target.getDistanceSq(player) < 25 && dialogue.entityHas(target))
                {
                    for (CDialogueBranch branch : dialogue.branches)
                    {
                        if (branch.sessionID.value.equals(packet.currentBranch.sessionID.value))
                        {
                            for (CDialogueChoice choice : branch.choices)
                            {
                                if (choice.text.value.equals(packet.choice.value))
                                {
                                    choice.execute(player);
                                }
                            }
                        }
                    }
                }
            });
            return null;
        }
    }
}

package com.fantasticsource.rpgquesting;

import com.fantasticsource.rpgquesting.actions.CActionEndDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogue;
import com.fantasticsource.rpgquesting.dialogue.CDialogueBranch;
import com.fantasticsource.rpgquesting.dialogue.CDialogueChoice;
import com.fantasticsource.rpgquesting.dialogue.CDialogues;
import com.fantasticsource.rpgquesting.gui.DialogueGUI;
import com.fantasticsource.rpgquesting.gui.DialoguesGUI;
import com.fantasticsource.rpgquesting.gui.JournalGUI;
import com.fantasticsource.rpgquesting.gui.MainEditorGUI;
import com.fantasticsource.rpgquesting.quest.CPlayerQuestData;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import com.fantasticsource.rpgquesting.quest.QuestTracker;
import com.fantasticsource.rpgquesting.quest.objective.CObjective;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.IObfuscatedComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
        WRAPPER.registerMessage(ActionErrorPacketHandler.class, ActionErrorPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestJournalDataPacketHandler.class, RequestJournalDataPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(JournalPacketHandler.class, JournalPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(QuestTrackerPacketHandler.class, QuestTrackerPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestTrackerChangePacketHandler.class, RequestTrackerChangePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(RequestAbandonQuestPacketHandler.class, RequestAbandonQuestPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(RequestDeleteQuestPacketHandler.class, RequestDeleteQuestPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(RequestSaveQuestPacketHandler.class, RequestSaveQuestPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(RequestEditorDataPacketHandler.class, RequestEditorDataPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(EditorPacketHandler.class, EditorPacket.class, discriminator++, Side.CLIENT);
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
        public ArrayList<CUUID> dialogueSessionIDs = new ArrayList<>();
        public ArrayList<CStringUTF8> dialogueDisplayNames = new ArrayList<>();

        public MultipleDialoguesPacket()
        {
            //Required
        }

        public MultipleDialoguesPacket(ArrayList<CDialogue> dialogues)
        {
            for (CDialogue dialogue : dialogues)
            {
                dialogueSessionIDs.add(dialogue.sessionID);
                dialogueDisplayNames.add(dialogue.name);
            }
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(dialogueSessionIDs.size());
            for (CUUID dialogueSessionID : dialogueSessionIDs) dialogueSessionID.write(buf);
            for (CStringUTF8 dialogueDisplayName : dialogueDisplayNames) dialogueDisplayName.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            int size = buf.readInt();
            for (int i = size; i > 0; i--)
            {
                dialogueSessionIDs.add(new CUUID().read(buf));
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
        CUUID dialogueSessionID;

        public ChooseDialoguePacket()
        {
            //Required
        }

        public ChooseDialoguePacket(CUUID dialogueSessionID)
        {
            this.dialogueSessionID = dialogueSessionID;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(CDialogues.targetID);
            dialogueSessionID.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            targetID = buf.readInt();
            dialogueSessionID = new CUUID().read(buf);
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
                CDialogue dialogue = CDialogues.getBySessionID(packet.dialogueSessionID.value);
                if (target != null && target.getDistanceSq(player) < 25 && dialogue.isAvailable(player, target))
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
                CDialogue dialogue = CDialogues.getBySessionID(packet.currentBranch.dialogueSessionID.value);
                if (target != null && target.getDistanceSq(player) < 25)
                {
                    for (CDialogueBranch branch : dialogue.branches)
                    {
                        if (branch.sessionID.value.equals(packet.currentBranch.sessionID.value))
                        {
                            for (CDialogueChoice choice : branch.choices)
                            {
                                if (choice.text.value.equals(packet.choice.value))
                                {
                                    if (dialogue.isAvailable(player, target) || choice.action.getClass() == CActionEndDialogue.class)
                                    {
                                        choice.execute(player);
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            });
            return null;
        }
    }


    public static class ActionErrorPacket implements IMessage
    {
        public ArrayList<String> error;

        public ActionErrorPacket()
        {
            //Required
        }

        public ActionErrorPacket(String choiceText, ArrayList<String> error)
        {
            this.error = error;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(error.size());
            for (String s : error) ByteBufUtils.writeUTF8String(buf, s);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            error = new ArrayList<>();
            for (int i = buf.readInt(); i > 0; i--) error.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    public static class ActionErrorPacketHandler implements IMessageHandler<ActionErrorPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ActionErrorPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> DialogueGUI.showChoiceActionError(packet));
            return null;
        }
    }


    public static class RequestJournalDataPacket implements IMessage
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

    public static class RequestJournalDataPacketHandler implements IMessageHandler<RequestJournalDataPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestJournalDataPacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() -> CQuests.syncJournal(ctx.getServerHandler().player, "", true));
            return null;
        }
    }


    public static class JournalPacket implements IMessage
    {
        public CPlayerQuestData data = new CPlayerQuestData();
        public String questToView = null;
        public boolean openGUI = false;

        public JournalPacket()
        {
            //Required
        }

        public JournalPacket(CPlayerQuestData playerQuestData, String questToView, boolean openGUI)
        {
            if (playerQuestData != null) data = playerQuestData;
            this.questToView = questToView == null ? "" : questToView;
            this.openGUI = openGUI;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            data.writeObf(buf);
            ByteBufUtils.writeUTF8String(buf, questToView);
            buf.writeBoolean(openGUI);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            data.readObf(buf);
            questToView = ByteBufUtils.readUTF8String(buf);
            openGUI = buf.readBoolean();
        }
    }

    public static class JournalPacketHandler implements IMessageHandler<JournalPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(JournalPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                if (packet.openGUI || JournalGUI.GUI.isVisible())
                {
                    String quest = packet.questToView;
                    if (quest.equals("")) JournalGUI.show(packet.data, JournalGUI.viewedQuest);
                    else JournalGUI.show(packet.data, quest);
                }
            });
            return null;
        }
    }


    public static class QuestTrackerPacket implements IMessage
    {
        public CStringUTF8 questName = new CStringUTF8();
        public ArrayList<CObjective> objectives = new ArrayList<>();

        public QuestTrackerPacket()
        {
            //Required
        }

        public QuestTrackerPacket(String questName, ArrayList<CObjective> objectives)
        {
            this.questName.set(questName);
            this.objectives = objectives;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            questName.write(buf);

            buf.writeInt(objectives.size());
            for (CObjective objective : objectives) IObfuscatedComponent.writeMarkedObf(buf, objective);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            questName.read(buf);

            for (int i = buf.readInt(); i > 0; i--) objectives.add((CObjective) IObfuscatedComponent.readMarkedObf(buf));
        }
    }

    public static class QuestTrackerPacketHandler implements IMessageHandler<QuestTrackerPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(QuestTrackerPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> QuestTracker.startTracking(packet.questName.value, packet.objectives));
            return null;
        }
    }


    public static class RequestTrackerChangePacket implements IMessage
    {
        String questName;

        public RequestTrackerChangePacket()
        {
            //Required
        }

        public RequestTrackerChangePacket(String questName)
        {
            this.questName = questName;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, questName);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            questName = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class RequestTrackerChangePacketHandler implements IMessageHandler<RequestTrackerChangePacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestTrackerChangePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> CQuests.track(ctx.getServerHandler().player, packet.questName));
            return null;
        }
    }


    public static class RequestAbandonQuestPacket implements IMessage
    {
        String questName;

        public RequestAbandonQuestPacket()
        {
            //Required
        }

        public RequestAbandonQuestPacket(String questName)
        {
            this.questName = questName;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, questName);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            questName = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class RequestAbandonQuestPacketHandler implements IMessageHandler<RequestAbandonQuestPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestAbandonQuestPacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() -> CQuests.abandon(ctx.getServerHandler().player, packet.questName));
            return null;
        }
    }


    public static class RequestDeleteQuestPacket implements IMessage
    {
        String questName;

        public RequestDeleteQuestPacket()
        {
            //Required
        }

        public RequestDeleteQuestPacket(String questName)
        {
            this.questName = questName;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, questName);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            questName = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class RequestDeleteQuestPacketHandler implements IMessageHandler<RequestDeleteQuestPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestDeleteQuestPacket packet, MessageContext ctx)
        {
            if (ctx.getServerHandler().player.interactionManager.getGameType() == GameType.CREATIVE)
            {
                CQuests.delete(packet.questName);
            }

            return null;
        }
    }


    public static class RequestSaveQuestPacket implements IMessage
    {
        CQuest quest = new CQuest();

        public RequestSaveQuestPacket()
        {
            //Required
        }

        public RequestSaveQuestPacket(CQuest quest)
        {
            this.quest = quest;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            quest.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            quest.read(buf);
        }
    }

    public static class RequestSaveQuestPacketHandler implements IMessageHandler<RequestSaveQuestPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestSaveQuestPacket packet, MessageContext ctx)
        {
            if (ctx.getServerHandler().player.interactionManager.getGameType() == GameType.CREATIVE)
            {
                CQuests.saveQuest(packet.quest);
            }

            return null;
        }
    }


    public static class RequestEditorDataPacket implements IMessage
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

    public static class RequestEditorDataPacketHandler implements IMessageHandler<RequestEditorDataPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestEditorDataPacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() -> CQuests.syncEditor(ctx.getServerHandler().player, true));
            return null;
        }
    }


    public static class EditorPacket implements IMessage
    {
        public boolean openGUI = false;
        LinkedHashMap<String, LinkedHashMap<String, CQuest>> allQuests = null;

        public EditorPacket()
        {
            //Required
        }

        public EditorPacket(boolean openGUI)
        {
            this.openGUI = openGUI;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(openGUI);

            LinkedHashMap<String, LinkedHashMap<String, CQuest>> map = CQuests.QUESTS.worldQuestDataByGroup;

            buf.writeInt(map.size());
            for (Map.Entry<String, LinkedHashMap<String, CQuest>> entry : map.entrySet())
            {
                ByteBufUtils.writeUTF8String(buf, entry.getKey());

                LinkedHashMap<String, CQuest> map2 = entry.getValue();
                buf.writeInt(map2.size());
                for (Map.Entry<String, CQuest> entry2 : map2.entrySet())
                {
                    ByteBufUtils.writeUTF8String(buf, entry2.getKey());
                    entry2.getValue().write(buf);
                }
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            openGUI = buf.readBoolean();

            allQuests = new LinkedHashMap<>();
            for (int i = buf.readInt(); i > 0; i--)
            {
                LinkedHashMap<String, CQuest> map = new LinkedHashMap<>();
                allQuests.put(ByteBufUtils.readUTF8String(buf), map);

                for (int i2 = buf.readInt(); i2 > 0; i2--)
                {
                    map.put(ByteBufUtils.readUTF8String(buf), new CQuest().read(buf));
                }
            }
        }
    }

    public static class EditorPacketHandler implements IMessageHandler<EditorPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(EditorPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> MainEditorGUI.show(packet.allQuests, null)); //TODO change null to actual data
            return null;
        }
    }
}

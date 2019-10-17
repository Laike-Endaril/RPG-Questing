package com.fantasticsource.rpgquesting;

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
import com.fantasticsource.rpgquesting.quest.objective.CObjectiveDialogue;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.IObfuscatedComponent;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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

import static com.fantasticsource.rpgquesting.OverheadIndicators.FUNC_SET_NONE;

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
        WRAPPER.registerMessage(RequestDeleteDialoguePacketHandler.class, RequestDeleteDialoguePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(RequestSaveDialoguePacketHandler.class, RequestSaveDialoguePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(OverheadIndicatorPacketHandler.class, OverheadIndicatorPacket.class, discriminator++, Side.CLIENT);
    }


    public static void branch(EntityPlayerMP player, boolean clear, CDialogueBranch branch)
    {
        WRAPPER.sendTo(new DialogueBranchPacket(clear, branch), player);

        CDialogue dialogue = CDialogues.get(branch.dialogueName.value);
        CObjectiveDialogue.onDialogue(player, dialogue.name.value, dialogue.branches.indexOf(branch));
    }

    public static class DialogueBranchPacket implements IMessage
    {
        public boolean clear;
        public CStringUTF8 paragraph = new CStringUTF8();
        public ArrayList<String> choices = new ArrayList<>();

        public DialogueBranchPacket()
        {
            //Required
        }

        private DialogueBranchPacket(boolean clear, CDialogueBranch branch)
        {
            this.clear = clear;
            this.paragraph.set(branch.paragraph.value);
            for (CDialogueChoice choice : branch.choices) choices.add(choice.text.value);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(clear);
            paragraph.write(buf);

            buf.writeInt(choices.size());
            for (String s : choices) ByteBufUtils.writeUTF8String(buf, s);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            clear = buf.readBoolean();
            paragraph.read(buf);

            for (int i = buf.readInt(); i > 0; i--) choices.add(ByteBufUtils.readUTF8String(buf));
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
        public ArrayList<String> dialogueNames = new ArrayList<>();

        public MultipleDialoguesPacket()
        {
            //Required
        }

        public MultipleDialoguesPacket(ArrayList<CDialogue> dialogues)
        {
            for (CDialogue dialogue : dialogues)
            {
                dialogueNames.add(dialogue.name.value);
            }
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(dialogueNames.size());
            for (String name : dialogueNames) ByteBufUtils.writeUTF8String(buf, name);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            for (int i = buf.readInt(); i > 0; i--) dialogueNames.add(ByteBufUtils.readUTF8String(buf));
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
        CStringUTF8 choice = new CStringUTF8();

        public ChooseDialoguePacket()
        {
            //Required
        }

        public ChooseDialoguePacket(String choice)
        {
            this.choice.set(choice);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            choice.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            choice.read(buf);
        }
    }

    public static class ChooseDialoguePacketHandler implements IMessageHandler<ChooseDialoguePacket, IMessage>
    {
        @Override
        public IMessage onMessage(ChooseDialoguePacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() -> CDialogues.tryStart(ctx.getServerHandler().player, packet.choice.value));
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
            Minecraft.getMinecraft().addScheduledTask(DialogueGUI.GUI::close);
            return null;
        }
    }


    public static class MakeChoicePacket implements IMessage
    {
        public CStringUTF8 choice = new CStringUTF8();

        public MakeChoicePacket()
        {
            //Required
        }

        public MakeChoicePacket(String choice)
        {
            this.choice.set(choice);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            choice.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            choice.read(buf);
        }
    }

    public static class MakeChoicePacketHandler implements IMessageHandler<MakeChoicePacket, IMessage>
    {
        @Override
        public IMessage onMessage(MakeChoicePacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() -> CDialogues.tryMakeChoice(ctx.getServerHandler().player, packet.choice.value));
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
        public LinkedHashMap<String, LinkedHashMap<String, CQuest>> allQuests = null;
        public LinkedHashMap<String, LinkedHashMap<String, CDialogue>> allDialogues = null;

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

            allQuests = CQuests.QUESTS.worldQuestDataByGroup;
            buf.writeInt(allQuests.size());
            for (Map.Entry<String, LinkedHashMap<String, CQuest>> group : allQuests.entrySet())
            {
                ByteBufUtils.writeUTF8String(buf, group.getKey());

                LinkedHashMap<String, CQuest> groupQuests = group.getValue();
                buf.writeInt(groupQuests.size());
                for (Map.Entry<String, CQuest> quest : groupQuests.entrySet())
                {
                    ByteBufUtils.writeUTF8String(buf, quest.getKey());
                    quest.getValue().write(buf);
                }
            }

            allDialogues = CDialogues.dialoguesByGroup;
            buf.writeInt(allDialogues.size());
            for (Map.Entry<String, LinkedHashMap<String, CDialogue>> group : allDialogues.entrySet())
            {
                ByteBufUtils.writeUTF8String(buf, group.getKey());

                LinkedHashMap<String, CDialogue> groupDialogues = group.getValue();
                buf.writeInt(groupDialogues.size());
                for (Map.Entry<String, CDialogue> dialogue : groupDialogues.entrySet())
                {
                    ByteBufUtils.writeUTF8String(buf, dialogue.getKey());
                    dialogue.getValue().write(buf);
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
                LinkedHashMap<String, CQuest> group = new LinkedHashMap<>();
                allQuests.put(ByteBufUtils.readUTF8String(buf), group);

                for (int i2 = buf.readInt(); i2 > 0; i2--)
                {
                    group.put(ByteBufUtils.readUTF8String(buf), new CQuest().read(buf));
                }
            }

            allDialogues = new LinkedHashMap<>();
            for (int i = buf.readInt(); i > 0; i--)
            {
                LinkedHashMap<String, CDialogue> group = new LinkedHashMap<>();
                allDialogues.put(ByteBufUtils.readUTF8String(buf), group);

                for (int i2 = buf.readInt(); i2 > 0; i2--)
                {
                    group.put(ByteBufUtils.readUTF8String(buf), new CDialogue().read(buf));
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
            Minecraft.getMinecraft().addScheduledTask(() -> MainEditorGUI.show(packet));
            return null;
        }
    }


    public static class RequestDeleteDialoguePacket implements IMessage
    {
        String dialogueName;

        public RequestDeleteDialoguePacket()
        {
            //Required
        }

        public RequestDeleteDialoguePacket(String dialogueName)
        {
            this.dialogueName = dialogueName;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, dialogueName);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            dialogueName = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class RequestDeleteDialoguePacketHandler implements IMessageHandler<RequestDeleteDialoguePacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestDeleteDialoguePacket packet, MessageContext ctx)
        {
            if (ctx.getServerHandler().player.interactionManager.getGameType() == GameType.CREATIVE)
            {
                CDialogues.delete(packet.dialogueName);
            }

            return null;
        }
    }


    public static class RequestSaveDialoguePacket implements IMessage
    {
        CDialogue dialogue = new CDialogue();

        public RequestSaveDialoguePacket()
        {
            //Required
        }

        public RequestSaveDialoguePacket(CDialogue quest)
        {
            this.dialogue = quest;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            dialogue.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            dialogue.read(buf);
        }
    }

    public static class RequestSaveDialoguePacketHandler implements IMessageHandler<RequestSaveDialoguePacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestSaveDialoguePacket packet, MessageContext ctx)
        {
            if (ctx.getServerHandler().player.interactionManager.getGameType() == GameType.CREATIVE)
            {
                CDialogues.saveDialogue(packet.dialogue);
            }

            return null;
        }
    }


    public static class OverheadIndicatorPacket implements IMessage
    {
        int entityID, function;

        public OverheadIndicatorPacket()
        {
            //Required
        }

        public OverheadIndicatorPacket(int entityID, int function)
        {
            this.entityID = entityID;
            this.function = function;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(entityID);
            buf.writeInt(function);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            entityID = buf.readInt();
            function = buf.readInt();
        }
    }

    public static class OverheadIndicatorPacketHandler implements IMessageHandler<OverheadIndicatorPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(OverheadIndicatorPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                if (packet.function == FUNC_SET_NONE) OverheadIndicators.overheadIndicators.remove(packet.entityID);
                else OverheadIndicators.overheadIndicators.put(packet.entityID, packet.function);
                System.out.println(Minecraft.getMinecraft().world.getEntityByID(packet.entityID).getName() + ", " + packet.function);
            });
            return null;
        }
    }
}

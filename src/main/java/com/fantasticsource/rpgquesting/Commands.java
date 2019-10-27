package com.fantasticsource.rpgquesting;

import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.rpgquesting.quest.CQuest;
import com.fantasticsource.rpgquesting.quest.CQuests;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.WHITE;

public class Commands extends CommandBase
{
    private static ArrayList<String> subcommands = new ArrayList<>();

    static
    {
        subcommands.addAll(Arrays.asList("get", "set"));
    }


    @Override
    public String getName()
    {
        return "rpgq";
    }

    @Override
    public List<String> getAliases()
    {
        ArrayList<String> names = new ArrayList<>();

        names.add("rpgquesting");

        return names;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return subUsage("");
    }

    public String subUsage(String subcommand)
    {
        if (!subcommands.contains(subcommand))
        {
            StringBuilder s = new StringBuilder(AQUA + "/" + getName() + " <" + subcommands.get(0));
            for (int i = 1; i < subcommands.size(); i++) s.append(" | ").append(subcommands.get(i));
            s.append(">");
            return s.toString();
        }

        switch (subcommand)
        {
            case "get":
                return AQUA + "/" + getName() + " " + subcommand + " <playername> <questname>" + WHITE + " - " + I18n.translateToLocalFormatted(RPGQuesting.MODID + ".cmd." + subcommand + ".comment");

            case "set":
                return AQUA + "/" + getName() + " " + subcommand + " <playername> <questname> unstarted|started|completed|uncompleted" + WHITE + " - " + I18n.translateToLocalFormatted(RPGQuesting.MODID + ".cmd." + subcommand + ".comment");

            default:
                return AQUA + "/" + getName() + " " + subcommand + WHITE + " - " + I18n.translateToLocalFormatted(RPGQuesting.MODID + ".cmd." + subcommand + ".comment");
        }
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 0) sender.getCommandSenderEntity().sendMessage(new TextComponentString(subUsage("")));
        else subCommand(sender, args);
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        ArrayList<String> result = new ArrayList<>();

        String partial = args[args.length - 1];
        switch (args.length)
        {
            case 1:
                result.addAll(subcommands);
                break;

            case 2:
                switch (args[0])
                {
                    case "get":
                    case "set":
                        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) result.add(player.getName());
                        break;
                }
                break;

            case 3:
                switch (args[0])
                {
                    case "get":
                    case "set":
                        for (CQuest quest : CQuests.QUESTS.worldQuestData.values()) result.add(quest.name.value.replaceAll(" ", "_"));
                        break;
                }
                break;

            case 4:
                switch (args[0])
                {
                    case "set":
                        result.addAll(Arrays.asList("unstarted", "started", "uncompleted", "completed"));
                        break;
                }
                break;
        }

        if (partial.length() != 0) result.removeIf(k -> partial.length() > k.length() || !k.substring(0, partial.length()).equalsIgnoreCase(partial));
        return result;
    }

    private void subCommand(ICommandSender sender, String[] args)
    {
        String cmd = args[0];
        switch (cmd)
        {
            case "get":
                if (args.length == 3)
                {
                    PlayerData data = PlayerData.get(args[1]);
                    CQuest quest = CQuests.get(args[2].replaceAll("_", " "));
                    if (data == null || data.player == null || quest == null)
                    {
                        notifyCommandListener(sender, this, subUsage(cmd));
                        return;
                    }


                    EntityPlayerMP player = (EntityPlayerMP) data.player;
                    if (quest.isReadyToComplete(player))
                    {
                        if (quest.isCompleted(player)) notifyCommandListener(sender, this, "fantasticlib.literal", "ready to complete (previously completed)");
                        else notifyCommandListener(sender, this, "fantasticlib.literal", "ready to complete");
                    }
                    else if (quest.isInProgress(player))
                    {
                        if (quest.isCompleted(player)) notifyCommandListener(sender, this, "fantasticlib.literal", "in progress (not ready to complete) (previously completed)");
                        else notifyCommandListener(sender, this, "fantasticlib.literal", "in progress (not ready to complete)");
                    }
                    else if (quest.isAvailable(player))
                    {
                        if (quest.isCompleted(player)) notifyCommandListener(sender, this, "fantasticlib.literal", "available (previously completed)");
                        else notifyCommandListener(sender, this, "fantasticlib.literal", "available");
                    }
                    else
                    {
                        if (quest.isCompleted(player)) notifyCommandListener(sender, this, "fantasticlib.literal", "not available (previously completed)");
                        else notifyCommandListener(sender, this, "fantasticlib.literal", "not available");
                    }
                }
                else notifyCommandListener(sender, this, subUsage(cmd));
                break;


            case "set":
                if (args.length == 4)
                {
                    PlayerData data = PlayerData.get(args[1]);
                    CQuest quest = CQuests.get(args[2].replaceAll("_", " "));
                    if (data == null || data.player == null || quest == null || !(args[3].equals("unstarted") || args[3].equals("started") || args[3].equals("uncompleted") || args[3].equals("completed")))
                    {
                        notifyCommandListener(sender, this, subUsage(cmd));
                        return;
                    }


                    EntityPlayerMP player = (EntityPlayerMP) data.player;
                    switch (args[3])
                    {
                        case "unstarted":
                            if (!quest.repeatable.value) CQuests.uncomplete(player, quest.name.value);
                            CQuests.abandon(player, quest.name.value);
                            break;

                        case "started":
                            if (!quest.repeatable.value) CQuests.uncomplete(player, quest.name.value);
                            CQuests.start(player, quest.name.value);
                            break;

                        case "uncompleted":
                            CQuests.uncomplete(player, quest.name.value);
                            break;

                        case "completed":
                            CQuests.forceCompleteNoReward(player, quest.name.value);
                            break;
                    }

                    notifyCommandListener(sender, this, "fantasticlib.success");
                }
                else notifyCommandListener(sender, this, subUsage(cmd));
                break;


            default:
                notifyCommandListener(sender, this, subUsage(cmd));
        }
    }
}

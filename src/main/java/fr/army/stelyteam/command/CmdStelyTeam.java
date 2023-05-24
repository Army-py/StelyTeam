package fr.army.stelyteam.command;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.TeamCache;
import fr.army.stelyteam.command.subcommand.help.SubCmdAdmin;
import fr.army.stelyteam.command.subcommand.help.SubCmdHelp;
import fr.army.stelyteam.command.subcommand.info.SubCmdInfo;
import fr.army.stelyteam.command.subcommand.info.SubCmdMoney;
import fr.army.stelyteam.command.subcommand.manage.*;
import fr.army.stelyteam.command.subcommand.member.SubCmdAddMember;
import fr.army.stelyteam.command.subcommand.member.SubCmdChangeOwner;
import fr.army.stelyteam.command.subcommand.member.SubCmdRemoveMember;
import fr.army.stelyteam.command.subcommand.team.SubCmdAccept;
import fr.army.stelyteam.command.subcommand.team.SubCmdDeny;
import fr.army.stelyteam.command.subcommand.utility.SubCmdHome;
import fr.army.stelyteam.command.subcommand.utility.SubCmdVisual;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CmdStelyTeam implements TabExecutor {

    private final TeamCache teamCache;
    private final CacheManager cacheManager;
    private final MessageManager messageManager;
    private final DatabaseManager databaseManager;
    private final Map<String, Object> subCommands;

    public CmdStelyTeam(@NotNull StelyTeamPlugin plugin) {
        teamCache = plugin.getTeamCache();
        cacheManager = plugin.getCacheManager();
        messageManager = plugin.getMessageManager();
        databaseManager = plugin.getDatabaseManager();
        subCommands = new HashMap<>();
        initSubCommands(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            // TODO Maybe send a message to the sender to express that he can't do the command as non-player
            return true;
        }

        if (cacheManager.isInConversation(player.getName())) {
            player.sendRawMessage(messageManager.getMessage("common.no_command_in_conv"));
            return true;
        }

        // TODO Inspect this
        /*
        if (!sqliteManager.isRegistered(player.getName())) {
            sqliteManager.registerPlayer(player);
        }*/

        if (args.length == 0) {
            final Optional<Team> team = teamCache.getPlayerTeam(player.getUniqueId());
            if (team.isEmpty()) {
                // TODO Send a message to tell that the player have no team
                return true;
            }
            StelyTeamPlugin.getPlugin().openMainInventory(player, team.get());
            return true;
        }

        // Delegate to a sub command
        if (subCommands.containsKey(args[0]) && !((SubCommand) subCommands.get(args[0])).isOpCommand()) {
            SubCommand subCmd = (SubCommand) subCommands.get(args[0]);
            if (subCmd.execute(player, args)) {
                return true;
            }
        } else if (sender.isOp()) {
            if (subCommands.containsKey(args[0]) && ((SubCommand) subCommands.get(args[0])).isOpCommand()) {
                SubCommand subCmd = (SubCommand) subCommands.get(args[0]);
                if (subCmd.execute(player, args)) {
                    return true;
                }
            }
        }

        player.sendMessage(messageManager.getMessage("common.invalid_command"));
        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            for (String subcommand : subCommands.keySet()) {
                if (subcommand.toLowerCase().toLowerCase().startsWith(args[0]) && !((SubCommand) subCommands.get(subcommand)).isOpCommand()) {
                    result.add(subcommand);
                }
            }
            if (sender.isOp()) {
                for (String subcommand : subCommands.keySet()) {
                    if (subcommand.toLowerCase().toLowerCase().startsWith(args[0]) && ((SubCommand) subCommands.get(subcommand)).isOpCommand()) {
                        result.add(subcommand);
                    }
                }
            }
            return result;
        } else if (args.length == 2) {
            if (sender.isOp()) {
                if (subCommands.containsKey(args[0]) && ((SubCommand) subCommands.get(args[0])).isOpCommand()) {
                    List<String> result = new ArrayList<>();
                    for (String teamID : /* TODO WOW !!!!! */databaseManager.getTeamsName()) {
                        if (teamID.toLowerCase().startsWith(args[1].toLowerCase())) {
                            result.add(teamID);
                        }
                    }
                    return result;
                }
            }
        }
        if (args.length > 1 && subCommands.containsKey(args[0].toLowerCase())) {
            List<String> results = ((SubCommand) subCommands.get(args[0])).onTabComplete(sender, args);
            return results;
        }
        return null;
    }


    private void initSubCommands(@NotNull StelyTeamPlugin plugin) {
        subCommands.put("home", new SubCmdHome(plugin));
        subCommands.put("visual", new SubCmdVisual(plugin));
        subCommands.put("info", new SubCmdInfo(plugin));
        subCommands.put("help", new SubCmdHelp(plugin));
        subCommands.put("accept", new SubCmdAccept(plugin));
        subCommands.put("deny", new SubCmdDeny(plugin));
        // subCommands.put("list", new SubCmdList(plugin));

        subCommands.put("admin", new SubCmdAdmin(plugin));
        subCommands.put("delete", new SubCmdDelete(plugin));
        subCommands.put("money", new SubCmdMoney(plugin));
        subCommands.put("upgrade", new SubCmdUpgrade(plugin));
        subCommands.put("downgrade", new SubCmdDowngrade(plugin));
        subCommands.put("editname", new SubCmdEditName(plugin));
        subCommands.put("editprefix", new SubCmdEditPrefix(plugin));
        subCommands.put("changeowner", new SubCmdChangeOwner(plugin));
        subCommands.put("addmember", new SubCmdAddMember(plugin));
        subCommands.put("removemember", new SubCmdRemoveMember(plugin));
    }

}

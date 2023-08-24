package fr.army.stelyteam.command;

import org.bukkit.command.PluginCommand;

import fr.army.stelyteam.StelyTeamPlugin;

public class CommandManager {
    private CmdStelyTeam stelyTeamCommand;
    private TeamChatCommand teamTchatCommand;

    public CommandManager(StelyTeamPlugin plugin) {
        PluginCommand stelyTeamCmd = plugin.getCommand("stelyteam");

        stelyTeamCommand = new CmdStelyTeam(plugin);
        stelyTeamCmd.setExecutor(stelyTeamCommand);
        stelyTeamCmd.setTabCompleter(stelyTeamCommand);

        PluginCommand teamTchatCmd = plugin.getCommand("teamtchat");

        teamTchatCommand = new TeamChatCommand(plugin, plugin.getTeamChatManager(), plugin.getCacheManager(),
                plugin.getMessageManager(), plugin.getServerNames(), plugin.getTeamChatFormat());
        teamTchatCmd.setExecutor(teamTchatCommand);
        teamTchatCmd.setTabCompleter(teamTchatCommand);
    }
}

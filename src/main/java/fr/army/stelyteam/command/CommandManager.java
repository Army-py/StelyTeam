package fr.army.stelyteam.command;

import org.bukkit.command.PluginCommand;

import fr.army.stelyteam.StelyTeamPlugin;

public class CommandManager {
    private CmdStelyTeam stelyTeamCommand;
    private CmdTeamTchat teamTchatCommand;

    public CommandManager(StelyTeamPlugin plugin) {
        PluginCommand stelyTeamCmd = plugin.getCommand("stelyteam");

        stelyTeamCommand = new CmdStelyTeam(plugin);
        stelyTeamCmd.setExecutor(stelyTeamCommand);
        stelyTeamCmd.setTabCompleter(stelyTeamCommand);

        PluginCommand teamTchatCmd = plugin.getCommand("teamtchat");

        teamTchatCommand = new CmdTeamTchat(plugin);
        teamTchatCmd.setExecutor(teamTchatCommand);
        teamTchatCmd.setTabCompleter(teamTchatCommand);
    }
}

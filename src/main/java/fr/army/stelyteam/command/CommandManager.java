package fr.army.stelyteam.command;

import org.bukkit.command.PluginCommand;

import fr.army.stelyteam.StelyTeamPlugin;

public class CommandManager {
    private CmdStelyTeam stelyTeamCommand;

    public CommandManager(StelyTeamPlugin plugin) {
        PluginCommand stelyTeamCmd = plugin.getCommand("stelyteam");

        stelyTeamCommand = new CmdStelyTeam(plugin);
        stelyTeamCmd.setExecutor(stelyTeamCommand);
        stelyTeamCmd.setTabCompleter(stelyTeamCommand);
    }
}

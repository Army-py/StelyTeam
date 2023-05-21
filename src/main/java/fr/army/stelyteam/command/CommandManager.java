package fr.army.stelyteam.command;

import fr.army.stelyteam.StelyTeamPlugin;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommandManager {

    public CommandManager(@NotNull StelyTeamPlugin plugin) {
        final PluginCommand stelyTeamCmd = Objects.requireNonNull(plugin.getCommand("stelyteam"));

        final CmdStelyTeam stelyTeamCommand = new CmdStelyTeam(plugin);
        stelyTeamCmd.setExecutor(stelyTeamCommand);
        stelyTeamCmd.setTabCompleter(stelyTeamCommand);

        final PluginCommand teamChatCmd = Objects.requireNonNull(plugin.getCommand("teamtchat"));

        final TeamChatCommand teamChatCommand = new TeamChatCommand(plugin.getTeamChatManager(), plugin.getCacheManager(), plugin.getMessageManager());
        teamChatCmd.setExecutor(teamChatCommand);
        teamChatCmd.setTabCompleter(teamChatCommand);
    }

}

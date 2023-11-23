package fr.army.stelyteam.command.subcommand.manage;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SubCmdDelete extends SubCommand {

    private final TeamManager teamManager;
    private final MessageManager messageManager;


    public SubCmdDelete(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        teamManager = plugin.getTeamManager();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_delete.usage"));
            return true;
        }
        args[0] = "";

        final String teamName = String.join("", args);
        final Team team = teamManager.getTeam(teamName);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        final UUID teamId = team.getId();
        teamManager.removeTeam(teamId);
        player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_delete.output", teamName));
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }


    @Override
    public boolean isOpCommand() {
        return true;
    }

}

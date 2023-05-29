package fr.army.stelyteam.command.subcommand.manage;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.TeamField;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SubCmdEditName extends SubCommand {

    private final TeamManager teamManager;
    //private DatabaseManager sqlManager;
    private final MessageManager messageManager;


    public SubCmdEditName(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        teamManager = plugin.getTeamManager();
        //this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        if (args.length < 3) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_editname.usage"));
            return true;
        }
        final String newName = args[2];
        if (newName.contains(" ")) {
            // TODO Check that because it's supposed impossible
            player.sendMessage(messageManager.getMessage("common.name_cannot_contain_space"));
            return true;
        }
        //args[0] = "";
        final Team team = teamManager.getTeam(args[1], TeamField.NAME);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        final Team newNameTeam = teamManager.getTeam(newName);
        if (newNameTeam != null) {
            player.sendMessage(messageManager.getMessage("common.name_already_exists"));
            return true;
        }
        team.getName().set(newName);
        teamManager.saveTeam(team);
        player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_editname.output", newName));
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

package fr.army.stelyteam.command.subcommand.member;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SubCmdAddMember extends SubCommand {

    private final StorageManager storageManager;
    private final DatabaseManager sqlManager;
    private final MessageManager messageManager;

    public SubCmdAddMember(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        //args[0] = "";

        if (args.length < 3) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_addmember.usage"));
            return true;
        }
        final Team team = storageManager.retreiveTeam(args[1]);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        // TODO Member work
        if (sqlManager.isMember(args[2])) {
            player.sendMessage(messageManager.getMessage("common.player_already_in_team"));
        } else {
            team.insertMember(args[2]);
            player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_addmember.output", args[2]));
        }
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

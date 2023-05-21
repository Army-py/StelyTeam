package fr.army.stelyteam.command.subcommand.manage;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SubCmdDelete extends SubCommand {

    private final StorageManager storageManager;
    private final MessageManager messageManager;


    public SubCmdDelete(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        storageManager = plugin.getStorageManager();
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
        final Team team = storageManager.retreiveTeam(teamName);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        // TODO Remove the team from the storage and from the cache
        //team.removeTeam();
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

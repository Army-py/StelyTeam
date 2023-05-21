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

public class SubCmdEditName extends SubCommand {

    private final StorageManager storageManager;
    //private DatabaseManager sqlManager;
    private final MessageManager messageManager;


    public SubCmdEditName(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        this.storageManager = plugin.getStorageManager();
        //this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        if (args.length < 3) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_editname.usage"));
            return true;
        }
        //args[0] = "";
        // TODO Work with cache
        final Team team = storageManager.retreiveTeam(args[1]);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        final String newName = args[2];
        if (newName.contains(" ")) {
            player.sendMessage(messageManager.getMessage("common.name_cannot_contain_space"));
            return true;
        }
        final Team newNameTeam = storageManager.retreiveTeam(newName);
        if (newNameTeam != null) {
            player.sendMessage(messageManager.getMessage("common.name_already_exists"));
            return true;
        }
        team.getName().set(newName);
        team.getName().save();
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

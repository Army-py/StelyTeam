package fr.army.stelyteam.command.subcommand.manage;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.cache.TeamCache;
import fr.army.stelyteam.cache.TeamField;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SubCmdEditPrefix extends SubCommand {

    private final StorageManager storageManager;
    private final TeamCache teamCache;
    private final MessageManager messageManager;
    private final ColorsBuilder colorsBuilder;

    public SubCmdEditPrefix(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        storageManager = plugin.getStorageManager();
        teamCache = plugin.getTeamCache();
        this.messageManager = plugin.getMessageManager();
        this.colorsBuilder = new ColorsBuilder(plugin);
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        //args[0] = "";

        if (args.length < 3) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_editprefix.usage"));
            return true;
        }
        Team team = storageManager.retreiveTeam(args[1], TeamField.PREFIX);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        final Team cachedTeam = teamCache.getTeam(team.getId());
        if (cachedTeam != null) {
            cachedTeam.getPrefix().retrieve(team.getId(), storageManager);
            team = cachedTeam;
        }
        final String newPrefix = args[2];
        team.getPrefix().set(newPrefix);
        team.getPrefix().save(team.getId(), storageManager);
        player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_editprefix.output", colorsBuilder.replaceColor(newPrefix)));
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

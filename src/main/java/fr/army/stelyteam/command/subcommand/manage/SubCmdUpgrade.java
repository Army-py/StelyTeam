package fr.army.stelyteam.command.subcommand.manage;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.cache.TeamCache;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SubCmdUpgrade extends SubCommand {

    private final StorageManager storageManager;
    private final TeamCache teamCache;
    private final YamlConfiguration config;
    private final MessageManager messageManager;

    public SubCmdUpgrade(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        storageManager = plugin.getStorageManager();
        teamCache = plugin.getTeamCache();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_upgrade.usage"));
            return true;
        }
        args[0] = "";
        final String teamName = String.join("", args);
        Team team = storageManager.retreiveTeam(teamName);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        final Team cachedTeam = teamCache.getTeam(team.getId());
        if (cachedTeam != null) {
            team = cachedTeam;
        }
        final int maxLevel = Objects.requireNonNull(config.getConfigurationSection("inventories.upgradeTotalMembers")).getValues(false).size() - 1;
        final Property<Integer> membersLevel = team.getUpgrades().getMembers();
        final Integer rawMemberLevel = membersLevel.get();
        final int membersLevelValue = rawMemberLevel == null ? 0 : rawMemberLevel;
        if (maxLevel == membersLevelValue) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_upgrade.max_level"));
            return true;
        }
        membersLevel.set(membersLevelValue + 1);
        membersLevel.save(team.getId(), storageManager);
        player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_upgrade.output", teamName));
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

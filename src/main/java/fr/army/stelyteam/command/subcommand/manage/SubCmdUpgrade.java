package fr.army.stelyteam.command.subcommand.manage;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.Property;
import fr.army.stelyteam.cache.SaveField;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SubCmdUpgrade extends SubCommand {

    private final TeamManager teamManager;
    private final YamlConfiguration config;
    private final MessageManager messageManager;

    public SubCmdUpgrade(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        teamManager = plugin.getTeamManager();
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
        final Team team = teamManager.getTeam(teamName, SaveField.UPGRADES_MEMBERS);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
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
        teamManager.saveTeam(team);
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
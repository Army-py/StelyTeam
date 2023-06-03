package fr.army.stelyteam.command.subcommand.member;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.SaveField;
import fr.army.stelyteam.cache.TeamCache;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamManager;
import fr.army.stelyteam.utils.OfflinePlayerRetriever;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SubCmdAddMember extends SubCommand {

    private final TeamManager teamManager;
    private final TeamCache teamCache;
    private final MessageManager messageManager;

    public SubCmdAddMember(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        teamManager = plugin.getTeamManager();
        teamCache = plugin.getTeamCache();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        //args[0] = "";

        if (args.length < 3) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_addmember.usage"));
            return true;
        }
        final Team team = teamManager.getTeam(args[1], SaveField.MEMBERS);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        final Optional<Team> currentTeam = teamCache.getPlayerTeam(player.getUniqueId());
        if (currentTeam.isPresent()) {
            player.sendMessage(messageManager.getMessage("common.player_already_in_team"));
            return true;
        }
        final OfflinePlayer target = OfflinePlayerRetriever.getOfflinePlayer(args[2]);
        final Member member = new Member(target.getUniqueId());
        member.getJoiningDate().set(Calendar.getInstance().getTime());
        // TODO Maybe set the rank
        team.getMembers().add(member);
        teamManager.saveTeam(team);
        player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_addmember.output", args[2]));
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

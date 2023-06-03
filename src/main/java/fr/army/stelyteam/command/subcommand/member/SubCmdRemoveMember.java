package fr.army.stelyteam.command.subcommand.member;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.SaveField;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SubCmdRemoveMember extends SubCommand {

    private final TeamManager teamManager;
    private final MessageManager messageManager;

    public SubCmdRemoveMember(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        teamManager = plugin.getTeamManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        //args[0] = "";
        if (args.length < 3) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_removemember.usage"));
            return true;
        }

        final Team team = teamManager.getTeam(args[1], SaveField.MEMBERS);
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }
        // TODO Member work
        if (!team.isTeamMember(args[2])) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_removemember.not_in_team"));
            return true;
        }
        team.removeMember(args[2]);
        teamManager.saveTeam(team);
        player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_removemember.output", args[2]));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        /*
        if (sender.isOp() && args.length == 3){
            if (args[0].equals("removemember")){
                Team team = Team.init(args[1]);
                List<String> result = new ArrayList<>();
                for (Member member : team.getTeamMembers()) {
                    String memberName = member.getMemberName();
                    Integer memberRank = member.getTeamRank();
                    if (memberRank > 0 && memberName.toLowerCase().startsWith(args[2].toLowerCase())){
                        result.add(memberName);
                    }
                }
                return result;
            }
        }
        return null;*/
        return Collections.emptyList();
    }

    @Override
    public boolean isOpCommand() {
        return true;
    }

}

package fr.army.stelyteam.command.subcommand.member;

import java.util.ArrayList;
import java.util.List;

import fr.army.stelyteam.cache.StorageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.jetbrains.annotations.NotNull;

public class SubCmdChangeOwner extends SubCommand {

    private final StorageManager storageManager;
    private final MessageManager messageManager;

    public SubCmdChangeOwner(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        this.storageManager = plugin.getStorageManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        //args[0] = "";

        if (args.length < 3){
            player.sendMessage(messageManager.getMessage("commands.stelyteam_changeowner.usage"));
            return true;
        }
        final Team team = storageManager.retreiveTeam(args[1]);
        if(team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }

        // TODO Member work
        if (team.isTeamMember(args[2])){
            Integer memberRank = team.getMemberRank(args[2]);
            if (memberRank != 0){
                String owner = team.getTeamOwnerName();
                team.updateTeamOwner(owner);
                player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_changeowner.output", args[2]));
            }else{
                player.sendMessage(messageManager.getMessage("commands.stelyteam_changeowner.already_owner"));
            }
        }else{
            player.sendMessage(messageManager.getMessage("commands.stelyteam_changeowner.not_in_team"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.isOp() && args.length == 3){
            if (args[0].equals("changeowner")){
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
        return null;
    }

    @Override
    public boolean isOpCommand() {
        return true;
    }

}

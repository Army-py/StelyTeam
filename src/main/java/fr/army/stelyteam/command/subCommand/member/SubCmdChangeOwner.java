package fr.army.stelyteam.command.subCommand.member;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

public class SubCmdChangeOwner extends SubCommand {

    private DatabaseManager sqlManager;
    private MessageManager messageManager;

    public SubCmdChangeOwner(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length < 3){
            player.sendMessage(messageManager.getMessage("commands.stelyteam_changeowner.usage"));
        }else{
            Team team = sqlManager.getTeamFromTeamName(args[1]);
            if (team != null){
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
            }else{
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.isOp() && args.length == 3){
            if (args[0].equals("changeowner")){
                Team team = sqlManager.getTeamFromTeamName(args[1]);
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

package fr.army.stelyteam.commands.subCommands.member;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.Member;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;

public class SubCmdRemoveMember extends SubCommand {

    private MySQLManager sqlManager;
    private MessageManager messageManager;

    public SubCmdRemoveMember(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length < 3){
            // player.sendMessage("Utilisation : /stelyteam removemember <nom de team> <membre>");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_removemember.usage"));
        }else{
            if (sqlManager.teamNameExists(args[1])){
                if (sqlManager.isMemberInTeam(args[2], args[1])){
                    sqlManager.removeMember(args[2], args[1]);
                    // player.sendMessage("Joueur retiré de la team");
                    player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_removemember.output", args[2]));
                }else{
                    // player.sendMessage("Ce joueur n'est pas dans cette team");
                    player.sendMessage(messageManager.getMessage("commands.stelyteam_removemember.not_in_team"));
                }
            }else{
                // player.sendMessage("Cette team n'existe pas");
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.isOp() && args.length == 3){
            if (args[0].equals("removemember")){
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

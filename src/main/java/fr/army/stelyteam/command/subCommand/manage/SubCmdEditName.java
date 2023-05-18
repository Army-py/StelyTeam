package fr.army.stelyteam.command.subcommand.manage;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

public class SubCmdEditName extends SubCommand {

    private DatabaseManager sqlManager;
    private MessageManager messageManager;


    public SubCmdEditName(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getDatabaseManager();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length < 3){
            player.sendMessage(messageManager.getMessage("commands.stelyteam_editname.usage"));
        }else{
            Team team = sqlManager.getTeamFromTeamName(args[1]);
            if (team != null){
                if (sqlManager.teamNameExists(args[2])){
                    player.sendMessage(messageManager.getMessage("common.name_already_exists"));
                }else if (args[2].contains(" ")){
                    player.sendMessage(messageManager.getMessage("common.name_cannot_contain_space"));
                }else{
                    team.updateTeamName(args[2]);
                    player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_editname.output", args[2]));
                }
            }else{
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            }
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }


    @Override
    public boolean isOpCommand() {
        return true;
    }

}

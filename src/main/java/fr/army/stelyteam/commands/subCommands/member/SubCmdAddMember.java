package fr.army.stelyteam.commands.subCommands.member;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;

public class SubCmdAddMember extends SubCommand {

    private SQLManager sqlManager;
    private MessageManager messageManager;

    public SubCmdAddMember(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length < 3){
            // player.sendMessage("Utilisation : /stelyteam addmember <nom de team> <membre>");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_addmember.usage"));
        }else{
            if (sqlManager.teamNameExists(args[1])){
                if (sqlManager.isMember(args[2])){
                    // player.sendMessage("Ce joueur est déjà dans une team");
                    player.sendMessage(messageManager.getMessage("common.player_already_in_team"));
                }else{
                    sqlManager.insertMember(args[2], args[1]);
                    // player.sendMessage("Joueur ajouté dans la team");
                    player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_addmember.output", args[2]));
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
        return null;
    }

    @Override
    public boolean isOpCommand() {
        return true;
    }

}

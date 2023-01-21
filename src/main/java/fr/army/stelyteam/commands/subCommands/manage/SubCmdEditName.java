package fr.army.stelyteam.commands.subCommands.manage;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;

public class SubCmdEditName extends SubCommand {

    private SQLManager sqlManager;
    private MessageManager messageManager;


    public SubCmdEditName(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length < 3){
            // player.sendMessage("Utilisation : /stelyteam editname <nom de team> <nouveau nom>");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_editname.usage"));
        }else{
            if (sqlManager.teamNameExists(args[1])){
                if (sqlManager.teamNameExists(args[2])){
                    // player.sendMessage("Ce nom de team existe déjà");
                    player.sendMessage(messageManager.getMessage("common.name_already_exists"));
                }else if (args[2].contains(" ")){
                    // player.sendMessage("Le nom de team ne doit pas contenir d'espace");
                    player.sendMessage(messageManager.getMessage("common.name_cannot_contain_space"));
                }else{
                    sqlManager.updateTeamName(args[1], args[2]);
                    // player.sendMessage("Nom de team modifié en " + args[2]);
                    player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_editname.output", args[2]));
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

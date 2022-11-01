package fr.army.stelyteam.commands.subCommands.manage;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;

public class SubCmdDowngrade extends SubCommand {
    private SQLManager sqlManager;
    private MessageManager messageManager;


    public SubCmdDowngrade(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length == 1){
            // player.sendMessage("Utilisation : /stelyteam downgrade <nom de team>");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_downgrade.usage"));
        }else{
            String teamID = String.join("", args);
            if (sqlManager.teamNameExists(teamID)){
                Integer teamUpgrades = sqlManager.getImprovLvlMembers(teamID);
                if (teamUpgrades == 0){
                    // player.sendMessage("Cette team est déjà au niveau minimum");
                    player.sendMessage(messageManager.getMessage("commands.stelyteam_downgrade.min_level"));
                }else{
                    // player.sendMessage("Nombre de membres diminué");
                    player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_downgrade.output", teamID));
                    sqlManager.decrementImprovLvlMembers(teamID);
                }
            }else{
                // player.sendMessage("Cette team n'existe pas");
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            }
        }
        return true;
    }
}

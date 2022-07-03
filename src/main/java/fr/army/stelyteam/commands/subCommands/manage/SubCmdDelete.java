package fr.army.stelyteam.commands.subCommands.manage;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;
import fr.army.stelyteam.utils.SQLiteManager;

public class SubCmdDelete extends SubCommand {
    private SQLManager sqlManager;
    private SQLiteManager sqliteManager;
    private MessageManager messageManager;


    public SubCmdDelete(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getSQLManager();
        this.sqliteManager = plugin.getSQLiteManager();
        this.messageManager = plugin.getMessageManager();
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length == 1){
            // player.sendMessage("Utilisation : /stelyteam delete <nom de team>");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_delete.usage"));
        }else{
            String teamID = String.join("", args);
            if (sqlManager.teamIdExist(teamID)){
                sqlManager.removeTeam(teamID);
                sqliteManager.removeHome(teamID);
                // player.sendMessage("Team supprimée");
                player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_delete.output", teamID));
            }else{
                // player.sendMessage("Cette team n'existe pas");
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            }
        }
        return true;
    }

}

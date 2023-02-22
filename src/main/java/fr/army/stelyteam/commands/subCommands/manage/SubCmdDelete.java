package fr.army.stelyteam.commands.subCommands.manage;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;
import fr.army.stelyteam.utils.manager.SQLiteManager;

public class SubCmdDelete extends SubCommand {
    private MySQLManager sqlManager;
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
            if (sqlManager.teamNameExists(teamID)){
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


    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }


    @Override
    public boolean isOpCommand() {
        return true;
    }

}

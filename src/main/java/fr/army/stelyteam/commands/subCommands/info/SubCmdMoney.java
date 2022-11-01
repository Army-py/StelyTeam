package fr.army.stelyteam.commands.subCommands.info;

import java.text.NumberFormat;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;

public class SubCmdMoney extends SubCommand {
    private SQLManager sqlManager;
    private MessageManager messageManager;

    public SubCmdMoney(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length == 1){
            // player.sendMessage("Utilisation : /stelyteam money <nom de team>");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_money.usage"));
        }else{
            String teamID = String.join("", args);
            if (sqlManager.teamNameExists(teamID)){
                // player.sendMessage(sqlManager.getTeamMoney(teamID).toString());
                player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_money.output", DoubleToString(sqlManager.getTeamMoney(teamID))));
            }else{
                // player.sendMessage("Cette team n'existe pas");
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            }
        }
        return true;
    }


    private String DoubleToString(double value){
        return NumberFormat.getNumberInstance(Locale.US).format(value);
    }
}

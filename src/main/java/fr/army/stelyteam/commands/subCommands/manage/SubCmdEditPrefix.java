package fr.army.stelyteam.commands.subCommands.manage;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.ColorsBuilder;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;

public class SubCmdEditPrefix extends SubCommand {

    private MessageManager messageManager;
    private SQLManager sqlManager;
    private ColorsBuilder colorsBuilder;

    public SubCmdEditPrefix(StelyTeamPlugin plugin) {
        super(plugin);
        this.messageManager = plugin.getMessageManager();
        this.sqlManager = plugin.getSQLManager();
        // this.colorsBuilder = plugin.getColorsBuilder();
        this.colorsBuilder = new ColorsBuilder();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length < 3){
            // player.sendMessage("Utilisation : /stelyteam editprefix <nom de team> <nouveau prefix>");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_editprefix.usage"));
        }else{
            if (sqlManager.teamIdExist(args[1])){
                sqlManager.updateTeamPrefix(args[1], args[2]);
                // player.sendMessage("Préfixe de team modifié en " + new ColorsBuilder().replaceColor(args[2]));
                player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_editprefix.output", colorsBuilder.replaceColor(args[2])));
            }else{
                // player.sendMessage("Cette team n'existe pas");
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            }
        }
        return true;
    }

}

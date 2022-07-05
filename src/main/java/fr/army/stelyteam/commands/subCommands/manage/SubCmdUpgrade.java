package fr.army.stelyteam.commands.subCommands.manage;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;

public class SubCmdUpgrade extends SubCommand {

    private SQLManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;

    public SubCmdUpgrade(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        args[0] = "";

        if (args.length == 1){
            // player.sendMessage("Utilisation : /stelyteam upgrade <nom de team>");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_upgrade.usage"));
        }else{
            String teamID = String.join("", args);
            if (sqlManager.teamIdExist(teamID)){
                Integer maxUpgrades = config.getConfigurationSection("inventories.upgradeTotalMembers").getValues(false).size() - 1;
                Integer teamUpgrades = sqlManager.getTeamLevel(teamID);
                if (maxUpgrades == teamUpgrades){
                    // player.sendMessage("Cette team est déjà au niveau maximum");
                    player.sendMessage(messageManager.getMessage("commands.stelyteam_upgrade.max_level"));
                }else{
                    sqlManager.incrementTeamLevel(teamID);
                    // player.sendMessage("Nombre de membres augmenté");
                    player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_upgrade.output", teamID));
                }
            }else{
                // player.sendMessage("Cette team n'existe pas");
                player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            }
        }
        return true;
    }

}

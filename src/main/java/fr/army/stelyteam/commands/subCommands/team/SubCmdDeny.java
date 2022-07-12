package fr.army.stelyteam.commands.subCommands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.MessageManager;

public class SubCmdDeny extends SubCommand {

    private MessageManager messageManager;

    public SubCmdDeny(StelyTeamPlugin plugin) {
        super(plugin);
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String playerName = player.getName();
        
        if (plugin.containTeamAction(playerName, "addMember")){
            plugin.removeTeamTempAction(playerName);
            // player.sendMessage("Vous avez refusé l'invitation");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_deny.output"));
        }else{
            // player.sendMessage("Vous n'avez pas d'invitation");
            player.sendMessage(messageManager.getMessage("common.no_invitation"));
        }

        return true;
    }
}

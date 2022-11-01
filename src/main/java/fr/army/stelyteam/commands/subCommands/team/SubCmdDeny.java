package fr.army.stelyteam.commands.subCommands.team;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.manager.MessageManager;

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
        String senderName = plugin.getTeamActions(playerName)[0];
        Player invitationSender = Bukkit.getPlayer(senderName);
        
        if (plugin.containTeamAction(playerName, "addMember") || plugin.containTeamAction(playerName, "addAlliance")){
            plugin.removeTeamTempAction(playerName);
            player.sendMessage(messageManager.getMessage("commands.stelyteam_deny.output"));
            if (invitationSender != null){
                invitationSender.sendMessage(messageManager.getReplaceMessage("sender.accepted_invitation", playerName));
            }
        }else{
            player.sendMessage(messageManager.getMessage("common.no_invitation"));
        }

        return true;
    }
}

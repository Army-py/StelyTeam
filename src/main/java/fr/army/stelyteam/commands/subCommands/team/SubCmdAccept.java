package fr.army.stelyteam.commands.subCommands.team;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;
import fr.army.stelyteam.utils.TeamMembersUtils;

public class SubCmdAccept extends SubCommand {

    private SQLManager sqlManager;
    private MessageManager messageManager;
    private TeamMembersUtils teamMembersUtils;

    public SubCmdAccept(StelyTeamPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
        this.teamMembersUtils = new TeamMembersUtils(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String playerName = player.getName();

        if (plugin.containTeamAction(playerName, "addMember")){
            String teamId = plugin.getTeamActions(playerName)[2];
            String senderName = plugin.getTeamActions(playerName)[0];
            plugin.removeTeamTempAction(playerName);
            sqlManager.insertMember(playerName, teamId);
            // player.sendMessage("Vous avez rejoint la team " + teamId);
            player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_accept.output", teamId));
            teamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
            // TeamMembersUtils.teamBroadcast(teamId, senderName, senderName + " a ajouté " + playerName + " à la team");
            teamMembersUtils.teamBroadcast(teamId, senderName, messageManager.getDoubleReplaceMessage("broadcasts.player_add_new_member", senderName, playerName));
        }else{
            player.sendMessage(messageManager.getMessage("common.no_invitation"));
            // player.sendMessage(messageManager.getMessage("receiver.no_invitation"));
        }

        return true;
    }
}

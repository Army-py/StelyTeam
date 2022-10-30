package fr.army.stelyteam.commands.subCommands.team;

import org.bukkit.Bukkit;
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
            Player invitationSender = Bukkit.getPlayer(senderName);
            plugin.removeTeamTempAction(playerName);
            player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_accept.output", teamId));
            if (invitationSender != null){
                invitationSender.sendMessage(messageManager.getReplaceMessage("sender.accepted_invitation", playerName));
            }
            teamMembersUtils.teamBroadcast(teamId, senderName, messageManager.replaceAuthorAndReceiver("broadcasts.player_add_new_member", senderName, playerName));
            sqlManager.insertMember(playerName, teamId);
            teamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
        }else if (plugin.containTeamAction(playerName, "addAlliance")){
            String senderName = plugin.getTeamActions(playerName)[0];
            Player invitationSender = Bukkit.getPlayer(senderName);
            String receiverName = plugin.getTeamActions(playerName)[1];
            String teamName = plugin.getTeamActions(playerName)[2];
            String allianceName = sqlManager.getTeamNameFromPlayerName(receiverName);
            plugin.removeTeamTempAction(playerName);
            player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_accept.output", teamName));
            if (invitationSender != null){
                invitationSender.sendMessage(messageManager.getReplaceMessage("sender.accepted_invitation", receiverName));
            }
            teamMembersUtils.teamBroadcast(teamName, senderName, messageManager.replaceAuthorAndTeamName("broadcasts.player_add_new_alliance", senderName, allianceName));
            sqlManager.insertAlliance(teamName, allianceName);
            teamMembersUtils.refreshTeamMembersInventory(teamName, playerName);
        }else{
            player.sendMessage(messageManager.getMessage("common.no_invitation"));
        }

        return true;
    }
}

package fr.army.stelyteam.commands.subCommands.team;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.TeamMembersUtils;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;

public class SubCmdAccept extends SubCommand {

    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private SQLManager sqlManager;
    private MessageManager messageManager;
    private TeamMembersUtils teamMembersUtils;

    public SubCmdAccept(StelyTeamPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
        this.teamMembersUtils = new TeamMembersUtils(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String playerName = player.getName();

        // if (plugin.containTeamAction(playerName, "addMember")){
        if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.ADD_MEMBER)){
            // String teamId = plugin.getTeamActions(playerName)[2];
            // String senderName = plugin.getTeamActions(playerName)[0];
            TemporaryAction tempAction = cacheManager.getTempAction(playerName);
            String teamName = tempAction.getTeam().getTeamName();
            String senderName = tempAction.getSenderName();
            Player invitationSender = Bukkit.getPlayer(senderName);
            // plugin.removeTeamTempAction(playerName);
            cacheManager.removePlayerAction(playerName);
            player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_accept.output", teamName));
            if (invitationSender != null){
                invitationSender.sendMessage(messageManager.getReplaceMessage("sender.accepted_invitation", playerName));
            }
            teamMembersUtils.teamBroadcast(teamName, senderName, messageManager.replaceAuthorAndReceiver("broadcasts.player_add_new_member", senderName, playerName));
            sqlManager.insertMember(playerName, teamName);
            teamMembersUtils.refreshTeamMembersInventory(teamName, playerName);
        // }else if (plugin.containTeamAction(playerName, "addAlliance")){
        }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.ADD_ALLIANCE)){
            // String senderName = plugin.getTeamActions(playerName)[0];
            // String receiverName = plugin.getTeamActions(playerName)[1];
            // String teamName = plugin.getTeamActions(playerName)[2];
            TemporaryAction tempAction = cacheManager.getTempAction(playerName);
            String senderName = tempAction.getSenderName();
            String receiverName = tempAction.getReceiverName();
            String teamName = tempAction.getTeam().getTeamName();
            Player invitationSender = Bukkit.getPlayer(senderName);
            String allianceName = sqlManager.getTeamNameFromPlayerName(receiverName);
            // plugin.removeTeamTempAction(playerName);
            cacheManager.removePlayerAction(playerName);
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

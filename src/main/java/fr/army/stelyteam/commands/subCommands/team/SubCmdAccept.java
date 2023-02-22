package fr.army.stelyteam.commands.subCommands.team;

import java.util.List;

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
import fr.army.stelyteam.utils.manager.MySQLManager;

public class SubCmdAccept extends SubCommand {

    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private MySQLManager sqlManager;
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

        if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.ADD_MEMBER)){
            TemporaryAction tempAction = cacheManager.getTempAction(playerName);
            String teamName = tempAction.getTeam().getTeamName();
            String senderName = tempAction.getSenderName();
            Player invitationSender = Bukkit.getPlayer(senderName);
            cacheManager.removePlayerAction(playerName);
            player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_accept.output", teamName));
            if (invitationSender != null){
                invitationSender.sendMessage(messageManager.getReplaceMessage("sender.accepted_invitation", playerName));
            }
            teamMembersUtils.teamBroadcast(teamName, senderName, messageManager.replaceAuthorAndReceiver("broadcasts.player_add_new_member", senderName, playerName));
            sqlManager.insertMember(playerName, teamName);
            teamMembersUtils.refreshTeamMembersInventory(teamName, playerName);
        }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.ADD_ALLIANCE)){
            TemporaryAction tempAction = cacheManager.getTempAction(playerName);
            String senderName = tempAction.getSenderName();
            String receiverName = tempAction.getReceiverName();
            String teamName = tempAction.getTeam().getTeamName();
            Player invitationSender = Bukkit.getPlayer(senderName);
            String allianceName = sqlManager.getTeamNameFromPlayerName(receiverName);
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

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isOpCommand() {
        return false;
    }
}

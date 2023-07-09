package fr.army.stelyteam.command.subCommand.team;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;

public class SubCmdAccept extends SubCommand {

    private CacheManager cacheManager;
    private MessageManager messageManager;

    public SubCmdAccept(StelyTeamPlugin plugin) {
        super(plugin);
        this.cacheManager = plugin.getCacheManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String playerName = player.getName();

        if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.ADD_MEMBER)){
            TemporaryAction tempAction = cacheManager.getTempAction(playerName);
            Team team = tempAction.getTeam();
            String teamName = team.getTeamName();
            String senderName = tempAction.getSenderName();
            Player invitationSender = Bukkit.getPlayer(senderName);
            cacheManager.removePlayerAction(playerName);
            player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_accept.output", teamName));
            if (invitationSender != null){
                invitationSender.sendMessage(messageManager.getReplaceMessage("sender.accepted_invitation", playerName));
            }
            team.teamBroadcast(senderName, messageManager.replaceAuthorAndReceiver("broadcasts.player_add_new_member", senderName, playerName));
            team.insertMember(playerName);
            team.refreshTeamMembersInventory(playerName);
            cacheManager.addTeam(team);
        }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.ADD_ALLIANCE)){
            TemporaryAction tempAction = cacheManager.getTempAction(playerName);
            String senderName = tempAction.getSenderName();
            String receiverName = tempAction.getTargetName();
            Team team = tempAction.getTeam();
            String teamName = team.getTeamName();
            Player invitationSender = Bukkit.getPlayer(senderName);
            Team alliance = Team.initFromPlayerName(receiverName);
            cacheManager.removePlayerAction(playerName);
            player.sendMessage(messageManager.getReplaceMessage("commands.stelyteam_accept.output", teamName));
            if (invitationSender != null){
                invitationSender.sendMessage(messageManager.getReplaceMessage("sender.accepted_invitation", receiverName));
            }
            team.teamBroadcast(senderName, messageManager.replaceAuthorAndTeamName("broadcasts.player_add_new_alliance", senderName, alliance.getTeamName()));
            team.insertAlliance(alliance);
            team.refreshTeamMembersInventory(playerName);
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

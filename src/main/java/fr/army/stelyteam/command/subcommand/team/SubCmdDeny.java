package fr.army.stelyteam.command.subcommand.team;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.jetbrains.annotations.NotNull;

public class SubCmdDeny extends SubCommand {

    private CacheManager cacheManager;
    private MessageManager messageManager;

    public SubCmdDeny(StelyTeamPlugin plugin) {
        super(plugin);
        this.cacheManager = plugin.getCacheManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        String playerName = player.getName();
        TemporaryAction tempAction = cacheManager.getTempAction(playerName);
        
        if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.ADD_MEMBER) || cacheManager.playerHasActionName(playerName, TemporaryActionNames.ADD_ALLIANCE)){
            String senderName = tempAction.getSenderName();
            Player invitationSender = Bukkit.getPlayer(senderName);
            cacheManager.removePlayerAction(playerName);
            player.sendMessage(messageManager.getMessage("commands.stelyteam_deny.output"));
            if (invitationSender != null){
                invitationSender.sendMessage(messageManager.getReplaceMessage("sender.refused_invitation", playerName));
            }
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

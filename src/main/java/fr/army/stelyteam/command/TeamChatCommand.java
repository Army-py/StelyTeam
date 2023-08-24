package fr.army.stelyteam.command;

import fr.army.stelyteam.chat.TeamChatManager;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class TeamChatCommand implements TabExecutor {

    private final TeamChatManager teamChatManager;
    private final CacheManager cacheManager;
    private final MessageManager messageManager;

    public TeamChatCommand(@NotNull TeamChatManager teamChatManager, @NotNull CacheManager cacheManager, @NotNull MessageManager messageManager) {
        this.teamChatManager = teamChatManager;
        this.cacheManager = cacheManager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            // Maybe send a message to the sender to express that he can't do the command as non-player
            return true;
        }

        if (cacheManager.isInConversation(player.getName())) {
            player.sendRawMessage(messageManager.getMessage("common.no_command_in_conv"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(messageManager.getMessage("teamchat.blank_message"));
            return true;
        }

        //Team team = Team.initFromPlayerName(playerName);
        String message = String.join(" ", args);

        
        int messageStart = 0;
        while (message.charAt(messageStart) == ' ') {
            messageStart++;
        }
        message = message.substring(messageStart);
        

        teamChatManager.sendMessage(player, message);

        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}

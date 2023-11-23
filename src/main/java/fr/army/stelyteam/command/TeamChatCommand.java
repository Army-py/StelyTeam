package fr.army.stelyteam.command;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.chat.TeamChatFormatHandler;
import fr.army.stelyteam.chat.TeamChatManager;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.network.ChannelRegistry;
import fr.army.stelyteam.utils.network.task.chat.AsyncChatSender;

public class TeamChatCommand implements TabExecutor {

    private final StelyTeamPlugin plugin;
    private final TeamChatManager teamChatManager;
    private final CacheManager cacheManager;

    private final String[] serverNames;
    private final String chatTeamFormat;

    public TeamChatCommand(@NotNull StelyTeamPlugin plugin, @NotNull TeamChatManager teamChatManager,
            @NotNull CacheManager cacheManager, @NotNull String[] serverNames, @NotNull String chatTeamFormat) {
        this.plugin = plugin;
        this.teamChatManager = teamChatManager;
        this.cacheManager = cacheManager;
        this.serverNames = serverNames;
        this.chatTeamFormat = chatTeamFormat;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            // TODO Maybe send a message to the sender to express that he can't do the command as non-player
            return true;
        }

        if (!Config.enableTeamChat) {
            player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
            return true;
        }
        
        final Team team = Team.init(player);

        if (team == null) {
            player.sendMessage(Messages.NO_TEAM.getMessage());
            return true;
        }

        if (cacheManager.isInConversation(player.getName())) {
            player.sendRawMessage(Messages.NO_COMMAND_IN_CONVERSATION.getMessage());
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(Messages.COMMAND_TEAMCHAT_BLANK_MESSAGE.getMessage());
            return true;
        }


        String message = String.join(" ", args);

        int messageStart = 0;
        while (message.charAt(messageStart) == ' ') {
            messageStart++;
        }
        message = message.substring(messageStart);

        if (message.isBlank()){
            player.sendMessage(Messages.COMMAND_TEAMCHAT_BLANK_MESSAGE.getMessage());
            return true;
        }


        final TeamChatFormatHandler formatHandler = new TeamChatFormatHandler();
        final String messageFormat = formatHandler.handle(player, team, chatTeamFormat, message);
        final AsyncChatSender asyncChatSender = new AsyncChatSender();
        final Set<UUID> recicipients = new HashSet<UUID>();
        recicipients.addAll(team.getMembersUuid());
        recicipients.addAll(plugin.getAllowedPlayers("essentials.chat.receive."+ChannelRegistry.TEAM_CHAT_CHANNEL.getChannel()));

        teamChatManager.sendMessage(player.getUniqueId(), messageFormat, recicipients);
        asyncChatSender.sendMessage(plugin, player, serverNames, messageFormat, plugin.getCurrentServerName(), team.getMembersUuid());

        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}

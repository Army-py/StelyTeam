package fr.army.stelyteam.command;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.chat.TeamChatFormatHandler;
import fr.army.stelyteam.chat.TeamChatManager;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.network.ChannelRegistry;
import fr.army.stelyteam.utils.network.task.chat.AsyncChatSender;

public class TeamChatCommand implements TabExecutor {

    private final StelyTeamPlugin plugin;
    private final YamlConfiguration config;
    private final TeamChatManager teamChatManager;
    private final CacheManager cacheManager;
    private final MessageManager messageManager;

    private final String[] serverNames;
    private final String chatTeamFormat;

    public TeamChatCommand(@NotNull StelyTeamPlugin plugin, @NotNull YamlConfiguration config, @NotNull TeamChatManager teamChatManager,
            @NotNull CacheManager cacheManager, @NotNull MessageManager messageManager, @NotNull String[] serverNames,
            @NotNull String chatTeamFormat) {
        this.plugin = plugin;
        this.config = config;
        this.teamChatManager = teamChatManager;
        this.cacheManager = cacheManager;
        this.messageManager = messageManager;
        this.serverNames = serverNames;
        this.chatTeamFormat = chatTeamFormat;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            // Maybe send a message to the sender to express that he can't do the command as non-player
            return true;
        }

        if (!config.getBoolean("allowTeamChat")) {
            player.sendMessage(messageManager.getMessage("common.functionnality_disabled"));
            return true;
        }
        
        final Team team = Team.init(player);

        if (team == null) {
            player.sendMessage(messageManager.getMessage("commands.teamChat.not_in_team"));
            return true;
        }

        if (cacheManager.isInConversation(player.getName())) {
            player.sendRawMessage(messageManager.getMessage("common.no_command_in_conv"));
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(messageManager.getMessage("commands.teamChat.blank_message"));
            return true;
        }


        String message = String.join(" ", args);

        int messageStart = 0;
        while (message.charAt(messageStart) == ' ') {
            messageStart++;
        }
        message = message.substring(messageStart);

        if (message.isBlank()){
            player.sendMessage(messageManager.getMessage("commands.teamChat.blank_message"));
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

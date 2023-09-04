package fr.army.stelyteam.chat;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.api.event.PlayerTeamChatEvent;
import fr.army.stelyteam.utils.builder.ColorsBuilder;

public class TeamChatProcessor {

    private final String format;

    public TeamChatProcessor(String format) {
        this.format = format;
    }

    public void process(@NotNull UUID senderUuid, @NotNull String messageFormat, @NotNull Set<UUID> recipients) {
        final PlayerTeamChatEvent event = new PlayerTeamChatEvent(!Bukkit.isPrimaryThread(), Bukkit.getOfflinePlayer(senderUuid), format, messageFormat);
        if (event.isCancelled()) {
            return;
        }
        for (UUID recipientUuid : recipients) {
            final Player receiver = Bukkit.getPlayer(recipientUuid);
            if (receiver == null) {
                continue;
            }
            receiver.sendMessage(ColorsBuilder.replaceColor(messageFormat)
                    .replace("&", ""));
        }
    }

}

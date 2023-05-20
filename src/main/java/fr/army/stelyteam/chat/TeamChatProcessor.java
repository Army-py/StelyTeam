package fr.army.stelyteam.chat;

import fr.army.stelyteam.api.event.PlayerTeamChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TeamChatProcessor {



    private final String format;

    public TeamChatProcessor(String format) {
        this.format = format;
    }

    public void process(@NotNull Player sender, @NotNull String message) {
        final PlayerTeamChatEvent event = new PlayerTeamChatEvent(!Bukkit.isPrimaryThread(), sender, format, message);
        if (event.isCancelled()) {
            return;
        }
        final TeamChatFormatHandler formatHandler = new TeamChatFormatHandler();
        final String toSendMessage = formatHandler.handle(sender, event.getFormat(), event.getMessage());
        // TODO Get all receivers (here probably team members)
        final Set<Player> recipients = new HashSet<>();
        for (Player receiver : recipients) {
            receiver.sendMessage(toSendMessage);
        }
    }

}

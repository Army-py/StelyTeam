package fr.army.stelyteam.listener.impl;

import fr.army.stelyteam.cache.TeamCache;
import fr.army.stelyteam.team.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChatPrefixListener implements Listener {

    private static final String PREFIX_PLACEHOLDER = "{STELYTEAM_PREFIX}";
    private final TeamCache teamCache;

    public ChatPrefixListener(@NotNull TeamCache teamCache) {
        this.teamCache = teamCache;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    private void onChat(AsyncPlayerChatEvent event) {
        final Optional<Team> team = teamCache.getPlayerTeam(event.getPlayer().getUniqueId());
        if (team.isEmpty()) {
            return;
        }
        final String prefix = team.get().getDisplayName().get();
        if (prefix == null) {
            return;
        }
        event.setFormat(event.getFormat().replace(PREFIX_PLACEHOLDER, prefix));
    }

}

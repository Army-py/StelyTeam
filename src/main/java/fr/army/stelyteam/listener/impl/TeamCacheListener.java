package fr.army.stelyteam.listener.impl;

import fr.army.stelyteam.cache.TeamCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class TeamCacheListener implements Listener {

    private final TeamCache teamCache;

    public TeamCacheListener(@NotNull TeamCache teamCache) {
        this.teamCache = teamCache;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
        teamCache.join(event.getPlayer());
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        teamCache.quit(event.getPlayer());
    }

}

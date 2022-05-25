package fr.army.stelyteam.listener;

import fr.army.stelyteam.storage.TeamCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerCacheListener implements Listener {

    private final TeamCache teamCache;

    public PlayerCacheListener(TeamCache teamCache) {
        this.teamCache = teamCache;
    }

    // TODO Load this listener somewhere with other listeners

    @EventHandler(priority = EventPriority.MONITOR)
    private void onJoin(PlayerJoinEvent event) {
        teamCache.loadPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onQuit(PlayerQuitEvent event) {
        teamCache.unloadPlayer(event.getPlayer().getUniqueId());
    }

}

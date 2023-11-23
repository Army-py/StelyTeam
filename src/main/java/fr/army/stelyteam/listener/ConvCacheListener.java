package fr.army.stelyteam.listener;

import fr.army.stelyteam.utils.manager.CacheManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class ConvCacheListener implements Listener {

    private final CacheManager cacheManager;

    public ConvCacheListener(@NotNull CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final String playerName = event.getPlayer().getName();
        if (cacheManager.getTempAction(playerName) != null) {
            cacheManager.removePlayerAction(playerName);
        }
    }

}

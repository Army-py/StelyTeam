package fr.army.stelyteam.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.manager.CacheManager;

public class PlayerQuit implements Listener{

    private CacheManager cacheManager;

    public PlayerQuit(StelyTeamPlugin plugin) {
        this.cacheManager = plugin.getCacheManager();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (cacheManager.getTempAction(playerName) != null){
            cacheManager.removePlayerAction(playerName);
        }
    }
}

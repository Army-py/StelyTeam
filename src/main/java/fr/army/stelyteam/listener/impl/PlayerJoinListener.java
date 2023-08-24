package fr.army.stelyteam.listener.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.CacheManager;

public class PlayerJoinListener implements Listener {
    
    private final CacheManager cacheManager;

    public PlayerJoinListener(StelyTeamPlugin plugin) {
        this.cacheManager = plugin.getCacheManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        final Player player = event.getPlayer();

        final Team team = Team.init(player);
        if (team == null) return;
        
        cacheManager.addTeam(team);
    }
}

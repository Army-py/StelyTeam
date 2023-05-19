package fr.army.stelyteam.listener;

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
        final String playerName = player.getName();

        final Team team = Team.initFromPlayerName(playerName);
        if (team == null) return;
        
        cacheManager.addTeam(team);
    }
}

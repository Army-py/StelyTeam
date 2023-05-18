package fr.army.stelyteam.listener;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

public class PlayerJoin implements Listener {
    
    private final DatabaseManager databaseManager;
    private final CacheManager cacheManager;

    public PlayerJoin(StelyTeamPlugin plugin) {
        this.databaseManager = plugin.getDatabaseManager();
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

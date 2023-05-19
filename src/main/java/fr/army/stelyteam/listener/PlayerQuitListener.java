package fr.army.stelyteam.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.CacheManager;

public class PlayerQuitListener implements Listener{

    private final CacheManager cacheManager;

    public PlayerQuitListener(StelyTeamPlugin plugin) {
        this.cacheManager = plugin.getCacheManager();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (cacheManager.getTempAction(playerName) != null){
            cacheManager.removePlayerAction(playerName);
        }

        Team team = cacheManager.getTeamByPlayerName(playerName);
        int count = 0;
        for (Member member : team.getTeamMembers()){
            if (!member.asPlayer().isOnline()){
                count++;
            }
        }
        if (count == team.getTeamMembers().size()){
            cacheManager.removeTeam(team);
        }
    }
}

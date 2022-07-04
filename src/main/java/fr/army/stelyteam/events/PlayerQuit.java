package fr.army.stelyteam.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.army.stelyteam.StelyTeamPlugin;

public class PlayerQuit implements Listener{

    private StelyTeamPlugin plugin;

    public PlayerQuit(StelyTeamPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (plugin.getTeamActions(playerName) != null){
            plugin.removeTeamTempAction(playerName);
        }
        if (plugin.getPlayerActions(playerName) != null){
            plugin.removePlayerTempAction(playerName);
        }
    }
}

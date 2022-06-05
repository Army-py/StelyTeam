package fr.army.stelyteam.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.army.stelyteam.StelyTeamPlugin;

public class PlayerQuit implements Listener{
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (StelyTeamPlugin.getTeamActions(playerName) != null){
            StelyTeamPlugin.removeTeamTempAction(playerName);
        }
        if (StelyTeamPlugin.getPlayerActions(playerName) != null){
            StelyTeamPlugin.removePlayerTempAction(playerName);
        }
    }
}

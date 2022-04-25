package fr.army.stelyteam.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.army.stelyteam.StelyTeamPlugin;

public class InventoryClose implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (org.bukkit.entity.Player) event.getPlayer();
        String playerName = player.getName();


        System.out.println("InventoryClose");
        StelyTeamPlugin.getTeamActions(playerName);
        if (event.getView().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.confirmInventory"))){
            if (StelyTeamPlugin.getTeamActions(playerName) != null) {
                StelyTeamPlugin.removeTeamTempAction(playerName);
            }
            if (StelyTeamPlugin.getPlayerActions(playerName) != null) {
                StelyTeamPlugin.removePlayerTempAction(playerName);
            }
        }
    }
}

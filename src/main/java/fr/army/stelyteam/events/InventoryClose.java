package fr.army.stelyteam.events;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.army.stelyteam.StelyTeamPlugin;

public class InventoryClose implements Listener {

    private StelyTeamPlugin plugin;
    private YamlConfiguration config;

    public InventoryClose(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (org.bukkit.entity.Player) event.getPlayer();
        String playerName = player.getName();

        if (event.getView().getTitle().equals(config.getString("inventoriesName.confirmInventory"))){
            if (plugin.getTeamActions(playerName) != null) {
                plugin.removeTeamTempAction(playerName);
            }
            if (plugin.getPlayerActions(playerName) != null) {
                plugin.removePlayerTempAction(playerName);
            }
        }
    }
}

package fr.army.stelyteam.events;


import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.inventories.ConfirmInventory;
import fr.army.stelyteam.events.inventories.StorageInventory;

public class InventoryCloseManager implements Listener {

    private StelyTeamPlugin plugin;
    private YamlConfiguration config;

    public InventoryCloseManager(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {        
        if (event.getView().getTitle().equals(config.getString("inventoriesName.confirmInventory"))){
            new ConfirmInventory(event, plugin).onInventoryClose();
        }else if (config.getConfigurationSection("inventoriesName.storages").getValues(true).containsValue(event.getView().getTitle())){
            new StorageInventory(event, plugin).onInventoryClose();
        }
    }
}

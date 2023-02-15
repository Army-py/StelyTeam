package fr.army.stelyteam.utils;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;

public abstract class TeamInventory {
    
    protected StelyTeamPlugin plugin;
    protected InventoryClickEvent clickEvent;
    protected InventoryCloseEvent closeEvent;

    public TeamInventory(StelyTeamPlugin plugin) {
        this.plugin = plugin;
    }

    public TeamInventory(StelyTeamPlugin plugin, InventoryClickEvent clickEvent){
        this.plugin = plugin;
        this.clickEvent = clickEvent;
    }

    public TeamInventory(StelyTeamPlugin plugin, InventoryCloseEvent closeEvent){
        this.plugin = plugin;
        this.closeEvent = closeEvent;
    }

    public abstract Inventory createInventory();

    public abstract Inventory createInventory(String playerName, Team team);

    public abstract Inventory createInventory(String playerName);

    public abstract Inventory createInventory(Team team);

    public abstract Inventory createInventory(Team team, Integer storageId);
}

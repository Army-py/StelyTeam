package fr.army.stelyteam.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.menu.view.MenuView;

public abstract class TeamMenu {
    
    private final String title;
    private final int size;

    public TeamMenu(String title, int size) {
        this.title = title;
        this.size = size;
    }

    public abstract MenuView createView(Player player);

    protected abstract Inventory createInventory();

    public abstract void onClick(InventoryClickEvent clickEvent);
}

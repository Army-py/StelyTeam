package fr.army.stelyteam.menu.view;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuView implements IMenuView {
    
    private final Player viewer;
    private final Inventory inventory;

    public MenuView(Player player, Inventory inventory) {
        this.viewer = player;
        this.inventory = inventory;
    }

    public Player getViewer() {
        return viewer;
    }

    public Inventory getInventory() {
        return inventory;
    }
}

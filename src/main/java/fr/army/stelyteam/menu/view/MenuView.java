package fr.army.stelyteam.menu.view;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.menu.TeamMenu;

public class MenuView implements IMenuView {
    
    private final Player viewer;
    private final TeamMenu menu;

    private Inventory inventory;

    public MenuView(Player player, TeamMenu menu) {
        this.viewer = player;
        this.menu = menu;
    }


    public Inventory createInventory() {
        final Inventory inventory = Bukkit.createInventory(this, menu.getSize(), menu.getTitle());

        // compete...

        return inventory;
    }


    public Player getViewer() {
        return viewer;
    }

    public TeamMenu getMenu() {
        return menu;
    }

    public Inventory getInventory() {
        return inventory;
    }
}

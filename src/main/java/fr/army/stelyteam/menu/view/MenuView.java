package fr.army.stelyteam.menu.view;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.button.Button;

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

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            final Button button = menu.getButton(slot);
            final ItemStack itemStack = button.getButtonItem().build();
            inventory.setItem(slot, itemStack);
        }

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

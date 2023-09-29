package fr.army.stelyteam.menu.view;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.menu.TeamMenu;

public abstract class AbstractMenuView<T extends AbstractMenuView<T>> implements IMenuView {
    
    protected final Player viewer;
    protected final TeamMenu<T> menu;

    protected Inventory inventory;

    public AbstractMenuView(Player player, TeamMenu<T> menu) {
        this.viewer = player;
        this.menu = menu;
    }


    public abstract Inventory createInventory();


    public void open(){
        viewer.openInventory(createInventory());
    }


    public Player getViewer() {
        return viewer;
    }

    public TeamMenu<T> getMenu() {
        return menu;
    }

    public Inventory getInventory() {
        return inventory;
    }
}

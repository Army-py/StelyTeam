package fr.army.stelyteam.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.view.MenuView;

public class EmptyMenu extends TeamMenu {
    
    private final String title;
    private final int size;

    public EmptyMenu(String title, int size) {
        super(title, size);
        this.title = title;
        this.size = size;
    }

    public MenuView createView(Player player) {
        return new MenuView(player, this);
    }

    public static EmptyMenu createInstance() {
        return new EmptyMenu("empty", 54);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
    }
}

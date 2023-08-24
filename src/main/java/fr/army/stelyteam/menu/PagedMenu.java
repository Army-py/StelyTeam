package fr.army.stelyteam.menu;

import org.bukkit.entity.Player;

public abstract class PagedMenu extends TeamMenu {

    public PagedMenu(Player viewer, int menuSlots, TeamMenu previousMenu) {
        super(viewer, menuSlots, previousMenu);
    }
    
    public PagedMenu(Player viewer, String menuName, int menuSlots, TeamMenu previousMenu) {
        super(viewer, menuName, menuSlots, previousMenu);
    }


    public abstract void openMenu(int pageId);
}

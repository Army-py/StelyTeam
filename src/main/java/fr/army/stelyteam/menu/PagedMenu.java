package fr.army.stelyteam.menu;

import org.bukkit.entity.Player;

public abstract class PagedMenu extends TeamMenuOLD {

    public PagedMenu(Player viewer, int menuSlots, TeamMenuOLD previousMenu) {
        super(viewer, menuSlots, previousMenu);
    }
    
    public PagedMenu(Player viewer, String menuName, int menuSlots, TeamMenuOLD previousMenu) {
        super(viewer, menuName, menuSlots, previousMenu);
    }


    public abstract void openMenu(int pageId);
}

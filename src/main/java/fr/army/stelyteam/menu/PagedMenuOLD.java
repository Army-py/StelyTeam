package fr.army.stelyteam.menu;

import org.bukkit.entity.Player;

public abstract class PagedMenuOLD extends TeamMenuOLD {

    public PagedMenuOLD(Player viewer, int menuSlots, TeamMenuOLD previousMenu) {
        super(viewer, menuSlots, previousMenu);
    }
    
    public PagedMenuOLD(Player viewer, String menuName, int menuSlots, TeamMenuOLD previousMenu) {
        super(viewer, menuName, menuSlots, previousMenu);
    }


    public abstract void openMenu(int pageId);
}

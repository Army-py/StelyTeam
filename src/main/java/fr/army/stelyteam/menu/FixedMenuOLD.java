package fr.army.stelyteam.menu;

import org.bukkit.entity.Player;

public abstract class FixedMenuOLD extends TeamMenuOLD {

    public FixedMenuOLD(Player viewer, int menuSlots, TeamMenuOLD previousMenu) {
        super(viewer, menuSlots, previousMenu);
    }

    public FixedMenuOLD(Player viewer, String menuName, int menuSlots, TeamMenuOLD previousMenu) {
        super(viewer, menuName, menuSlots, previousMenu);
    }


    // public abstract void openMenu(@Nullable Team team);
}

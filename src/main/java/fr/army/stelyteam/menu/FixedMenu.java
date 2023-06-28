package fr.army.stelyteam.menu;

import org.bukkit.entity.Player;

public abstract class FixedMenu extends TeamMenu {

    public FixedMenu(Player viewer, int menuSlots, TeamMenu previousMenu) {
        super(viewer, menuSlots, previousMenu);
    }

    public FixedMenu(Player viewer, String menuName, int menuSlots, TeamMenu previousMenu) {
        super(viewer, menuName, menuSlots, previousMenu);
    }


    // public abstract void openMenu(@Nullable Team team);
}

package fr.army.stelyteam.menu.view.impl;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import fr.army.stelyteam.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class PagedMenuView extends AbstractMenuView<PagedMenuView> {

    private final Team team;

    public PagedMenuView(@NotNull Player player, @NotNull TeamMenu<PagedMenuView> menu, @NotNull Team team) {
        super(player, menu);
        this.team = team;
    }

    @Override
    public Inventory createInventory() {
        return null;
    }
}

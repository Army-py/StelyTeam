package fr.army.stelyteam.menu;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.menu.view.MenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;

public abstract class TeamMenu<T extends MenuView> {
    
    private final MenuBuilderResult menuBuilderResult;

    public TeamMenu(MenuBuilderResult menuBuilderResult) {
        this.menuBuilderResult = menuBuilderResult;
    }

    public abstract T createView(Player player, Optional<Team> team);

    public abstract void onClick(InventoryClickEvent clickEvent);


    @NotNull
    public MenuBuilderResult getMenuBuilderResult() {
        return menuBuilderResult;
    }
}

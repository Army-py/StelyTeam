package fr.army.stelyteam.menu;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.menu.view.AbstractMenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;

public abstract class TeamMenu<T extends AbstractMenuView<T>> {

    protected final MenuBuilderResult<T> menuBuilderResult;

    public TeamMenu(@NotNull MenuBuilderResult<T> menuBuilderResult) {
        this.menuBuilderResult = menuBuilderResult;
    }

    public abstract T createView(Player player, Optional<Team> team);

    public abstract void onClick(InventoryClickEvent clickEvent);


    @NotNull
    public MenuBuilderResult<T> getMenuBuilderResult() {
        return menuBuilderResult;
    }
}

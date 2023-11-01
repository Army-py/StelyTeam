package fr.army.stelyteam.menu.impl;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.view.TeamMenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;

public class ConfirmAddMoneyMenu extends TeamMenu<TeamMenuView> {

    public ConfirmAddMoneyMenu(@NotNull MenuBuilderResult<TeamMenuView> menuBuilderResult) {
        super(menuBuilderResult);
    }

    @Override
    public TeamMenuView createView(Player player, Optional<Team> team) {
        return new TeamMenuView(player, this, team.orElse(null));
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
    }
    
}

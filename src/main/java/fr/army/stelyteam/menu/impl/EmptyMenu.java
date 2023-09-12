package fr.army.stelyteam.menu.impl;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.template.MenuTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;

public class EmptyMenu extends TeamMenu<AbstractMenuView> {

    private EmptyMenu(MenuBuilderResult menuBuilderResult) {
        super(menuBuilderResult);
    }

    @Override
    public AbstractMenuView createView(Player player, Optional<Team> team) {
        return new AbstractMenuView(player, this);
    }

    public static EmptyMenu createInstance() {
        return new EmptyMenu("empty", 54);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
    }
}

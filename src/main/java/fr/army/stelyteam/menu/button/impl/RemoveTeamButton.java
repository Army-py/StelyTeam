package fr.army.stelyteam.menu.button.impl;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import fr.army.stelyteam.menu.view.impl.TeamMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class RemoveTeamButton extends Button<TeamMenuView> {
    public RemoveTeamButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        // TODO: Remove team
    }

    @Override
    public @NotNull Button<TeamMenuView> get(@NotNull ButtonTemplate buttonTemplate) {
        return new RemoveTeamButton(buttonTemplate);
    }
}

package fr.army.stelyteam.menu.button.impl;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.impl.TeamMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TeamStoragesButton extends Button<TeamMenuView> {
    public TeamStoragesButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        // TODO: open team storages menu
    }
}

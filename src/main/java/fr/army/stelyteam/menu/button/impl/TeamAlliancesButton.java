package fr.army.stelyteam.menu.button.impl;

import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.impl.TeamMenuView;

public class TeamAlliancesButton extends Button<TeamMenuView> {

    public TeamAlliancesButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        // TODO: open team alliances menu
    }
    
}

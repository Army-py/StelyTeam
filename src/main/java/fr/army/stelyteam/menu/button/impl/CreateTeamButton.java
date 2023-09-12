package fr.army.stelyteam.menu.button.impl;

import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.TeamMenuView;

public class CreateTeamButton extends Button<TeamMenuView> {

    public CreateTeamButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
    }

    
}

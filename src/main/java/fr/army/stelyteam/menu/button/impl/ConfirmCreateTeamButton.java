package fr.army.stelyteam.menu.button.impl;

import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.MenuView;

public class ConfirmCreateTeamButton extends Button<MenuView> {

    public ConfirmCreateTeamButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        // TODO: add instructions to create team
    }
    
}

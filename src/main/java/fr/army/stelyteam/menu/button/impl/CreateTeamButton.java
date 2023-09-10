package fr.army.stelyteam.menu.button.impl;

import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.container.ButtonContainer;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.team.Team;

public class CreateTeamButton extends Button<ButtonContainer<Team>> {

    public CreateTeamButton(ButtonTemplate buttonTemplate, ButtonContainer<Team> buttonContainer) {
        super(buttonTemplate, buttonContainer);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
    }

    
}

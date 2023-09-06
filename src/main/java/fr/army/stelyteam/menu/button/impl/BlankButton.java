package fr.army.stelyteam.menu.button.impl;

import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.ButtonItem;

public class BlankButton extends Button {

    public BlankButton(char character, ButtonItem item) {
        super(character, item);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
    }
    
}

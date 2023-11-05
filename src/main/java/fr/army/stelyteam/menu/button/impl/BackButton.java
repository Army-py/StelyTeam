package fr.army.stelyteam.menu.button.impl;

import fr.army.stelyteam.menu.view.impl.MenuView;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import org.jetbrains.annotations.NotNull;

public class BackButton extends Button<MenuView> {

    public BackButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        clickEvent.getWhoClicked().closeInventory();
    }

    @Override
    public @NotNull Button<MenuView> get(@NotNull ButtonTemplate buttonTemplate) {
        return new BackButton(buttonTemplate);
    }
}

package fr.army.stelyteam.menu.button.impl;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.impl.PagedMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PreviousPageButton extends Button<PagedMenuView> {
    public PreviousPageButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {

    }
}

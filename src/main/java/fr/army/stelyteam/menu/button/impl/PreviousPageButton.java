package fr.army.stelyteam.menu.button.impl;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import fr.army.stelyteam.menu.view.impl.PagedMenuView;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class PreviousPageButton<C> extends Button<PagedMenuView<C>> {
    public PreviousPageButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {

    }

    @Override
    public @NotNull Button<PagedMenuView<C>> get(@NotNull ButtonTemplate buttonTemplate) {
        return new PreviousPageButton<>(buttonTemplate);
    }
}

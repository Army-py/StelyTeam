package fr.army.stelyteam.menu.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;

public abstract class Button<T extends AbstractMenuView<T>> {
    
    private final ButtonTemplate buttonTemplate;
    
    private T menuView;
    
    public Button(ButtonTemplate buttonTemplate) {
        this.buttonTemplate = buttonTemplate;
    }

    public abstract void onClick(InventoryClickEvent clickEvent);


    public Button<T> setButtonItem(ButtonItem buttonItem) {
        buttonTemplate.setButtonItem(buttonItem);
        return this;
    }


    @NotNull
    public ButtonTemplate getButtonTemplate() {
        return buttonTemplate;
    }

    @Nullable
    public T getMenuView() {
        return menuView;
    }

    public Button<T> setMenuView(T menuView) {
        this.menuView = menuView;
        return this;
    }
}

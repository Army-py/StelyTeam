package fr.army.stelyteam.menu.button;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.menu.button.container.ButtonContainer;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.MenuView;

public abstract class Button<T extends ButtonContainer<?>> {
    
    private final ButtonTemplate buttonTemplate;
    private final T buttonContainer;

    private MenuView menuView;
    
    public Button(ButtonTemplate buttonTemplate, T buttonContainer) {
        this.buttonTemplate = buttonTemplate;
        this.buttonContainer = buttonContainer;
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

    @NotNull
    public T getButtonContainer() {
        return buttonContainer;
    }

    @Nullable
    public MenuView getMenuView() {
        return menuView;
    }

    public Button<T> setMenuView(MenuView menuView) {
        this.menuView = menuView;
        return this;
    }
}

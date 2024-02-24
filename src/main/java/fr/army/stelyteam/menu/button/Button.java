package fr.army.stelyteam.menu.button;

import fr.army.stelyteam.menu.button.impl.AddMoneyButton;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;

public abstract class Button<T extends AbstractMenuView<T>> implements ButtonSupplier {
    
    protected final ButtonTemplate buttonTemplate;
    
    protected T menuView;
    
    public Button(ButtonTemplate buttonTemplate) {
        this.buttonTemplate = buttonTemplate;
    }

    public abstract void onClick(InventoryClickEvent clickEvent);


    public Button<T> setButtonItem(@NotNull ButtonItem buttonItem) {
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

    // protected void openPreviousMenu(Optional<Team> team){
    //     final MenuTemplate<T> menuTemplate = menuView.getMenu().getMenuBuilderResult().getMenuTemplate();
    //     final Player viewer = menuView.getViewer();
    //     if (menuTemplate.canPrecede() && menuTemplate.getPrecedingMenu() != null){
    //         menuTemplate.getPrecedingMenu().createView(viewer, team).open();
    //     }else{
    //         viewer.closeInventory();
    //     }
    // }
}

package fr.army.stelyteam.menu.button;

import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class Button {
    
    private final char character;

    private ButtonItem buttonItem;

    public Button(char character, ButtonItem buttonItem) {
        this.character = character;
        this.buttonItem = buttonItem;
    }

    public char getCharacter() {
        return character;
    }

    public ButtonItem getButtonItem() {
        return buttonItem;
    }

    public Button setButtonItem(ButtonItem buttonItem) {
        this.buttonItem = buttonItem;
        return this;
    }

    public abstract void onClick(InventoryClickEvent clickEvent);
}

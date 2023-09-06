package fr.army.stelyteam.menu.button;

import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class Button {
    
    private final char character;
    private final ButtonItem item;

    public Button(char character, ButtonItem item) {
        this.character = character;
        this.item = item;
    }

    public char getCharacter() {
        return character;
    }

    public ButtonItem getItem() {
        return item;
    }

    public abstract void onClick(InventoryClickEvent clickEvent);
}

package fr.army.stelyteam.menu.button.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.menu.button.ButtonItem;

public class ButtonTemplate {
    
    private final char character;
    private ButtonItem buttonItem;

    public ButtonTemplate(char character, ButtonItem buttonItem) {
        this.character = character;
        this.buttonItem = buttonItem;
    }

    @NotNull
    public char getCharacter() {
        return character;
    }

    @Nullable
    public ButtonItem getButtonItem() {
        return buttonItem;
    }

    public ButtonTemplate setButtonItem(ButtonItem buttonItem) {
        this.buttonItem = buttonItem;
        return this;
    }
}

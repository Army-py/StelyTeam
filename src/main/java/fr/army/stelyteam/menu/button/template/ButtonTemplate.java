package fr.army.stelyteam.menu.button.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.army.stelyteam.menu.button.ButtonItem;

public class ButtonTemplate {
    
    private final char character;
    private ButtonItem buttonItem;

    public ButtonTemplate(char character, @NotNull ButtonItem buttonItem) {
        this.character = character;
        this.buttonItem = buttonItem;
    }

    public char getCharacter() {
        return character;
    }

    @NotNull
    public ButtonItem getButtonItem() {
        return buttonItem;
    }

    public ButtonTemplate setButtonItem(@NotNull ButtonItem buttonItem) {
        this.buttonItem = buttonItem;
        return this;
    }
}

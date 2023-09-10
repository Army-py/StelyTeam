package fr.army.stelyteam.menu.template;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.impl.BlankButton;

public class MenuTemplate {
    
    private final String title;
    private final int size;
    private final Button[] buttons;

    public MenuTemplate(String title, int size) {
        this.title = title;
        this.size = size;
        this.buttons = new Button[size];
    }

    @NotNull
    public Button getButton(int slot) {
        return buttons[slot];
    }

    public void addButtons(Button... buttons){
        for (int i = 0; i < buttons.length; i++) {
            this.buttons[i] = buttons[i];
        }
    }

    public void mapButton(int slot, Button button) {
        if (!(button instanceof BlankButton))
            this.buttons[slot] = button;
        else{
            this.buttons[slot] = button.setButtonItem(this.buttons[slot].getButtonTemplate().getButtonItem());
        }
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public int getSize() {
        return size;
    }

    @NotNull
    public Button[] getButtons() {
        return buttons;
    }
}

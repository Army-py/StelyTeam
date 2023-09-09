package fr.army.stelyteam.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.impl.BlankButton;
import fr.army.stelyteam.menu.view.MenuView;

public abstract class TeamMenu {
    
    private final String title;
    private final int size;
    private final Button[] buttons;

    public TeamMenu(String title, int size) {
        this.title = title;
        this.size = size;
        this.buttons = new Button[size];
    }

    public abstract MenuView createView(Player player);

    public abstract void onClick(InventoryClickEvent clickEvent);


    public Button getButton(int slot) {
        return buttons[slot];
    }

    public void addButtons(Button... buttons){
        for (int i = 0; i < buttons.length; i++) {
            this.buttons[i] = buttons[i];
        }
    }

    public void mapButton(int slot, Button button) {
        if (button instanceof BlankButton)
            this.buttons[slot] = button;
        else
            this.buttons[slot] = button.setButtonItem(this.buttons[slot].getButtonItem());
    }



    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public Button[] getButtons() {
        return buttons;
    }
}

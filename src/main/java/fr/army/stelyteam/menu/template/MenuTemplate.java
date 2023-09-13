package fr.army.stelyteam.menu.template;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.impl.BlankButton;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;

public class MenuTemplate<T extends AbstractMenuView<T>> {
    
    private final String title;
    private final int size;
    private final Button<T>[] buttons;

    public MenuTemplate(String title, int size) {
        this.title = title;
        this.size = size;
        this.buttons = new Button[size];
    }

    @NotNull
    public Button<T> getButton(int slot) {
        return buttons[slot];
    }

    public void addButton(Button<T> button, int slot){
        this.buttons[slot] = button;
    }

    public void addButtons(Button<T>[] buttons){
        for (int i = 0; i < buttons.length; i++) {
            this.buttons[i] = buttons[i];
        }
    }

    public void mapButton(int slot, Button<T> button) {
        if (!(button instanceof BlankButton))
            this.buttons[slot] = button;
        else{
            this.buttons[slot] = button.setButtonItem(this.buttons[slot].getButtonTemplate().getButtonItem());
        }
    }

    public void mapButtons(int[] slots, Button<T> button) {
        for (int slot : slots) {
            mapButton(slot, button);
        }
    }

    public int[] getSlots(char itemSection) {
        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i < buttons.length; i++) {
            final ButtonTemplate buttonTemplate = buttons[i].getButtonTemplate();
            if (buttonTemplate.getCharacter() == itemSection)
                slots.add(i);
        }

        return slots.stream().mapToInt(i -> i).toArray();
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
    public Button<T>[] getButtons() {
        return buttons;
    }
}
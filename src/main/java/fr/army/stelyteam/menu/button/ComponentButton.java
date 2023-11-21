package fr.army.stelyteam.menu.button;

import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;

public abstract class ComponentButton<T extends AbstractMenuView<T>, C> extends Button<T> {
    public ComponentButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    public abstract void replacePlaceholders(C component);
}

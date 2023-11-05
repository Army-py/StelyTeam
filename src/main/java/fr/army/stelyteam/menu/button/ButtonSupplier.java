package fr.army.stelyteam.menu.button;

import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import org.jetbrains.annotations.NotNull;

public interface ButtonSupplier {

    @NotNull
    Button<? extends AbstractMenuView<?>> get(@NotNull ButtonTemplate buttonTemplate);

}

package fr.army.stelyteam.menu.impl;

import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.menu.button.impl.BlankButton;
import fr.army.stelyteam.menu.button.impl.CancelButton;
import fr.army.stelyteam.menu.button.impl.ConfirmCreateTeamButton;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.template.MenuTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import fr.army.stelyteam.menu.view.MenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.menu.MenuBuilder;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;

public class EmptyMenu extends TeamMenu<MenuView> {

    private EmptyMenu(MenuBuilderResult<MenuView> menuBuilderResult) {
        super(menuBuilderResult);
    }

    @Override
    public MenuView createView(Player player, Optional<Team> team) {
        return new MenuView(player, this);
    }

    public static EmptyMenu createInstance() {
        // final MenuBuilderResult<MenuView> builderResult = MenuBuilder.getInstance().loadMenu(configName + ".yml");
        // final MenuTemplate<MenuView> menuTemplate = builderResult.getMenuTemplate();
        // final YamlConfiguration config = builderResult.getConfig();

        // for (String chrSection : config.getConfigurationSection("items").getKeys(false)) {
        //     final char chr = chrSection.charAt(0);
        //     final ConfigurationSection itemSection = config.getConfigurationSection("items." + chr);
        //     final Buttons buttonType = Buttons.getButtonType(itemSection.getString("button-type"));
            
        //     final ButtonTemplate buttonTemplate = new ButtonTemplate(chr, null);
        //     final Button<MenuView> button;
        //     switch (buttonType) {
        //         default: 
        //             button = new BlankButton<>(buttonTemplate);
        //             break;
        //     }

        //     menuTemplate.mapButtons(menuTemplate.getSlots(chr), button);
        // }

        // return new ConfirmCreateTeamMenu(builderResult);
        return null;
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
    }
}

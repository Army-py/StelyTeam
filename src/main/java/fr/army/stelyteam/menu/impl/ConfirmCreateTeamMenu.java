package fr.army.stelyteam.menu.impl;

import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.template.MenuTemplate;
import fr.army.stelyteam.menu.view.impl.MenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.menu.MenuBuilder;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConfirmCreateTeamMenu extends TeamMenu<MenuView> {

    public ConfirmCreateTeamMenu(@NotNull MenuBuilderResult<MenuView> menuBuilderResult) {
        super(menuBuilderResult);
    }

    @Override
    public MenuView createView(Player player, Optional<Team> team) {
        return new MenuView(player, this);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        
    }


    public static ConfirmCreateTeamMenu createInstance(String configName) {
        final MenuBuilderResult<MenuView> builderResult = MenuBuilder.getInstance().loadMenu(configName + ".yml");
        final MenuTemplate<MenuView> menuTemplate = builderResult.getMenuTemplate();
        final YamlConfiguration config = builderResult.getConfig();

        if (config == null) return new ConfirmCreateTeamMenu(builderResult);

//        for (String chrSection : config.getConfigurationSection("items").getKeys(false)) {
//            final char chr = chrSection.charAt(0);
//            final ConfigurationSection itemSection = config.getConfigurationSection("items." + chr);
//
//            final ButtonTemplate buttonTemplate = new ButtonTemplate(chr, null);
//            final Button<MenuView> button;
//            final Buttons buttonType = Buttons.getButtonType(itemSection.getString("button-type"));
//            switch (buttonType) {
//                case BUTTON_CONFIRM_CREATE_TEAM:
//                    button = new ConfirmCreateTeamButton(buttonTemplate);
//                    break;
//                case BUTTON_CANCEL:
//                    button = new CancelButton(buttonTemplate);
//                    break;
//
//                default:
//                    button = new BlankButton(buttonTemplate);
//                    break;
//            }
//
//            menuTemplate.mapButtons(menuTemplate.getSlots(chr), button);
//        }
        menuTemplate.setPrecedingMenu(Menus.MENU_CREATE_TEAM);

        return new ConfirmCreateTeamMenu(builderResult);
    }
}

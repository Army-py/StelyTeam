package fr.army.stelyteam.menu.impl.temp_new;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.view.impl.MenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.menu.MenuBuilder;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;


public class CreateTeamMenu extends TeamMenu<MenuView> {

    private CreateTeamMenu(MenuBuilderResult<MenuView> menuBuilderResult) {
        super(menuBuilderResult);
    }


    @Override
    public MenuView createView(Player player, Optional<Team> team) {
        return new MenuView(player, this);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
    }

    public static CreateTeamMenu createInstance(String configName) {
        final MenuBuilderResult<MenuView> builderResult = MenuBuilder.getInstance().loadMenu(configName + ".yml");
//        final MenuTemplate<MenuView> menuTemplate = builderResult.getMenuTemplate();
//        final YamlConfiguration config = builderResult.getConfig();
//
//        if (config == null) return new CreateTeamMenu(builderResult);
//
//        for (String chrSection : config.getConfigurationSection("items").getKeys(false)) {
//            final char chr = chrSection.charAt(0);
//            final ConfigurationSection itemSection = config.getConfigurationSection("items." + chr);
//            final Buttons buttonType = Buttons.getButtonType(itemSection.getString("button-type"));
//
//            final ButtonTemplate buttonTemplate = new ButtonTemplate(chr, null);
//            final Button<MenuView> button;
//            switch (buttonType) {
//                case BUTTON_CREATE_TEAM:
//                    button = new CreateTeamButton(buttonTemplate);
//                    break;
//                case BUTTON_BACK:
//                    button = new BackButton<>(buttonTemplate);
//                    break;
//
//                default:
//                    button = new BlankButton<>(buttonTemplate);
//                    break;
//            }
//
//            menuTemplate.mapButtons(menuTemplate.getSlots(chr), button);
//        }

        return new CreateTeamMenu(builderResult);
    }
}

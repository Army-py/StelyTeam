package fr.army.stelyteam.menu.impl;

import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.menu.button.impl.BackButton;
import fr.army.stelyteam.menu.button.impl.BlankButton;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.template.MenuTemplate;
import fr.army.stelyteam.menu.view.TeamMenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.menu.MenuBuilder;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;

public class MainMenu extends TeamMenu<TeamMenuView> {

    public MainMenu(@NotNull MenuBuilderResult<TeamMenuView> menuBuilderResult) {
        super(menuBuilderResult);
    }

    @Override
    public TeamMenuView createView(Player player, Optional<Team> team) {
        return new TeamMenuView(player, this, team.orElse(null));
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
    }
    

    public static MainMenu createInstance(String configName){
        final MenuBuilderResult<TeamMenuView> builderResult = MenuBuilder.getInstance().loadMenu(configName + ".yml");
        final MenuTemplate<TeamMenuView> menuTemplate = builderResult.getMenuTemplate();
        final YamlConfiguration config = builderResult.getConfig();

        if (config == null) return new MainMenu(builderResult);

        for (String chrSection : config.getConfigurationSection("items").getKeys(false)) {
            final char chr = chrSection.charAt(0);
            final ConfigurationSection itemSection = config.getConfigurationSection("items." + chr);
            final Buttons buttonType = Buttons.getButtonType(itemSection.getString("button-type"));
            
            final ButtonTemplate buttonTemplate = new ButtonTemplate(chr, null);
            final Button<TeamMenuView> button;
            switch (buttonType) {
                case BUTTON_BACK:
                    button = new BackButton<>(buttonTemplate);
                    break;
                case BUTTON_TEAM_DETAILS:
                    button = new BlankButton<>(buttonTemplate);
                    break;
            
                default: 
                    button = new BlankButton<>(buttonTemplate);
                    break;
            }
            
            menuTemplate.mapButtons(menuTemplate.getSlots(chr), button);
        }

        return new MainMenu(builderResult);
    }
}

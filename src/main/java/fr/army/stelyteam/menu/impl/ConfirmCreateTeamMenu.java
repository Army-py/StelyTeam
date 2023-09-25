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
import fr.army.stelyteam.menu.button.impl.BlankButton;
import fr.army.stelyteam.menu.button.impl.CancelButton;
import fr.army.stelyteam.menu.button.impl.ConfirmCreateTeamButton;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.template.MenuTemplate;
import fr.army.stelyteam.menu.view.TeamMenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.menu.MenuBuilder;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;

public class ConfirmCreateTeamMenu extends TeamMenu<TeamMenuView> {

    public ConfirmCreateTeamMenu(@NotNull MenuBuilderResult<TeamMenuView> menuBuilderResult) {
        super(menuBuilderResult);
    }

    @Override
    public TeamMenuView createView(Player player, Optional<Team> team) {
        return new TeamMenuView(player, this, team.orElse(null));
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        
    }


    public static ConfirmCreateTeamMenu createInstance(String configName) {
        final MenuBuilderResult<TeamMenuView> builderResult = MenuBuilder.getInstance().loadMenu(configName + ".yml");
        final MenuTemplate<TeamMenuView> menuTemplate = builderResult.getMenuTemplate();
        final YamlConfiguration config = builderResult.getConfig();

        for (String chrSection : config.getConfigurationSection("items").getKeys(false)) {
            final char chr = chrSection.charAt(0);
            final ConfigurationSection itemSection = config.getConfigurationSection("items." + chr);
            final Buttons buttonType = Buttons.getButtonType(itemSection.getString("button-type"));
            
            final ButtonTemplate buttonTemplate = new ButtonTemplate(chr, null);
            final Button<TeamMenuView> button;
            switch (buttonType) {
                case BUTTON_CONFIRM_CREATE_TEAM:
                    button = new ConfirmCreateTeamButton(buttonTemplate);
                    break;
                case BUTTON_CANCEL:
                    button = new CancelButton<>(buttonTemplate);
                    break;
            
                default: 
                    button = new BlankButton<>(buttonTemplate);
                    break;
            }

            menuTemplate.mapButtons(menuTemplate.getSlots(chr), button);
        }

        return new ConfirmCreateTeamMenu(builderResult);
    }
}

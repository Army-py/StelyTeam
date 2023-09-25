package fr.army.stelyteam.menu.button.impl;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.MenuView;

public class CreateTeamButton extends Button<MenuView> {

    private final StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();

    public CreateTeamButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        final Player player = (Player) clickEvent.getWhoClicked();

        final double teamCreationPrice = Config.priceCreateTeam;
        if (plugin.getEconomyManager().hasEnough(player, teamCreationPrice)){
            Menus.MENU_CONFIRM_CREATE_TEAM.createView(player, Optional.empty());
        }else{
            player.sendMessage(Messages.PREFIX.getMessage() + Messages.NOT_ENOUGH_MONEY.getMessage());
        }
    }
}

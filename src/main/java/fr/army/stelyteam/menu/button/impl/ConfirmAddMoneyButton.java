package fr.army.stelyteam.menu.button.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.TeamMenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.EconomyManager;

public class ConfirmAddMoneyButton extends Button<TeamMenuView> {

    private final EconomyManager economy = StelyTeamPlugin.getPlugin().getEconomyManager();

    public ConfirmAddMoneyButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        final Player player = (Player) clickEvent.getWhoClicked();
        final Team team = this.getMenuView().getTeam();

        economy.removeMoneyPlayer(player, money);
        team.incrementTeamMoney(money);
        player.sendMessage(Messages.PREFIX.getMessage() + Messages.TEAM_BANK_MONEY_ADDED.getMessage());
    }
    
}

package fr.army.stelyteam.menu.button.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.TeamMenuView;
import fr.army.stelyteam.team.Team;

public class CreateTeamButton extends Button<TeamMenuView> {

    private final StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();

    public CreateTeamButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        final Player player = (Player) clickEvent.getWhoClicked();
        final Team team = getMenuView().getTeam();

        final double teamCreationPrice = Config.priceCreateTeam;
        if (plugin.getEconomyManager().hasEnough(player, teamCreationPrice)){
            // TODO: add instructions to create team
        }else{
            // TODO: Review messages
            // TODO: add instructions to say that player doesn't have enough money
        }
    }

    
}

package fr.army.stelyteam.menu.view;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.team.Team;

public class TeamMenuView extends MenuView {
    
    private final Team team;

    public TeamMenuView(Player player, Inventory inventory, Team team) {
        super(player, inventory);
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }
}

package fr.army.stelyteam.menu.view;

import org.bukkit.entity.Player;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.team.Team;

public class TeamMenuView extends MenuView {
    
    private final Team team;

    public TeamMenuView(Player player, TeamMenu menu, Team team) {
        super(player, menu);
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }
}

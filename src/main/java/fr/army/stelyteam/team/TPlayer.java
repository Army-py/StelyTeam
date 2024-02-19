package fr.army.stelyteam.team;

import org.bukkit.entity.Player;

import java.util.Optional;

public class TPlayer {

    private final Player player;
    private final Optional<Team> team;

    public TPlayer(Player player, Optional<Team> team) {
        this.player = player;
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public Optional<Team> getTeam() {
        return team;
    }
}

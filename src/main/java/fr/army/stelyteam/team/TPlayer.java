package fr.army.stelyteam.team;

import fr.army.stelyteam.utils.manager.EconomyManager;
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

    public boolean hasTeam() {
        return team.isPresent();
    }

    public void deposit(double amount) {
        EconomyManager.addMoneyPlayer(player, amount);
    }

    public void withdraw(double amount) {
         if (EconomyManager.checkMoneyPlayer(player, amount)) {
             EconomyManager.removeMoneyPlayer(player, amount);
         }
         throw new IllegalStateException("Player does not have enough money");
    }

}

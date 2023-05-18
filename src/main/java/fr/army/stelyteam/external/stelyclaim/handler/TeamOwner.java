package fr.army.stelyteam.external.stelyclaim.handler;

import java.util.Collections;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.flowsqy.stelyclaim.api.ClaimOwner;

public class TeamOwner implements ClaimOwner {

    private final OfflinePlayer player;

    public TeamOwner(OfflinePlayer player) {
        this.player = player;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    @Override
    public Set<OfflinePlayer> getMailable() {
        return Collections.singleton(player);
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public boolean own(Player player) {
        return this.player.equals(player);
    }
}

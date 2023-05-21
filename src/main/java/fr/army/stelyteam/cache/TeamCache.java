package fr.army.stelyteam.cache;

import fr.army.stelyteam.team.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TeamCache {

    private final StorageManager storageManager;
    private final Map<UUID, Team> cachedTeams;
    private final Map<UUID, Optional<Team>> playersTeam;

    public Team getTeam(@NotNull UUID teamId) {
        final Team cachedTeam = cachedTeams.get(teamId);
        if (cachedTeam == null) {
            throw new IllegalStateException("Team '" + teamId + "' is not loaded");
        }
        return cachedTeam;
    }

    public Optional<Team> getPlayerTeam(@NotNull UUID playerId) {
        final Optional<Team> cachedTeam = playersTeam.get(playerId);
        if (cachedTeam == null) {
            throw new IllegalStateException("Player '" + playerId + "' 's team is not loaded");
        }
        return cachedTeam;
    }

    public void join(@NotNull Player player) {
        final Team team = storageManager.retreivePlayerTeam(player.getName());
        if (team == null) {
            playersTeam.put(player.getUniqueId(), Optional.empty());
            return;
        }
        final Team cachedTeam = cachedTeams.computeIfAbsent(team.getId(), k -> team);
        playersTeam.put(player.getUniqueId(), Optional.of(cachedTeam));
    }

    public void quit(@NotNull Player player) {
        final Optional<Team> team = playersTeam.remove(player.getUniqueId());
        if (team.isEmpty()) {
            return;
        }
        if (playersTeam.containsValue(team)) {
            return;
        }
        cachedTeams.remove(team.get().getId());
    }

}

package fr.army.stelyteam.cache;

import fr.army.stelyteam.team.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TeamCache {

    private final StorageManager storageManager;
    private final Lock lock;
    private final Map<UUID, Team> cachedTeams;
    private final Map<UUID, Optional<Team>> playersTeam;

    public TeamCache(@NotNull StorageManager storageManager) {
        this.storageManager = storageManager;
        lock = new ReentrantLock();
        cachedTeams = new HashMap<>();
        playersTeam = new HashMap<>();
    }

    public Team getTeam(@NotNull UUID teamId) {
        try {
            lock.lock();
            final Team cachedTeam = cachedTeams.get(teamId);
            if (cachedTeam == null) {
                throw new IllegalStateException("Team '" + teamId + "' is not loaded");
            }
            return cachedTeam;
        } finally {
            lock.unlock();
        }
    }

    public Optional<Team> getPlayerTeam(@NotNull UUID playerId) {
        try {
            lock.lock();
            final Optional<Team> cachedTeam = playersTeam.get(playerId);
            if (cachedTeam == null) {
                throw new IllegalStateException("Player '" + playerId + "' 's team is not loaded");
            }
            return cachedTeam;
        } finally {
            lock.unlock();
        }
    }

    public void join(@NotNull Player player) {
        try {
            lock.lock();
            final Team team = storageManager.retreivePlayerTeam(player.getName());
            if (team == null) {
                playersTeam.put(player.getUniqueId(), Optional.empty());
                return;
            }
            final Team cachedTeam = cachedTeams.computeIfAbsent(team.getId(), k -> team);
            playersTeam.put(player.getUniqueId(), Optional.of(cachedTeam));
        } finally {
            lock.unlock();
        }
    }

    public void quit(@NotNull Player player) {
        try {
            lock.lock();
            final Optional<Team> team = playersTeam.remove(player.getUniqueId());
            if (team.isEmpty()) {
                return;
            }
            if (playersTeam.containsValue(team)) {
                return;
            }
            cachedTeams.remove(team.get().getId());
        } finally {
            lock.unlock();
        }
    }

}

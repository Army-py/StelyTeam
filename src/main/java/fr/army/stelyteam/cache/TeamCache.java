package fr.army.stelyteam.cache;

import fr.army.stelyteam.team.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

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
            return cachedTeams.get(teamId);
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

    public void remove(@NotNull UUID teamId) {
        try {
            lock.lock();
            final Team team = cachedTeams.remove(teamId);
            if (team == null) {
                return;
            }
            removeInMap(playersTeam, value -> value.isPresent() && value.get().getId().equals(teamId));
        } finally {
            lock.unlock();
        }
    }

    private <K, V> void removeInMap(Map<K, V> map, Predicate<V> equalityCheck) {
        final LinkedList<K> toRemovePlayer = new LinkedList<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (equalityCheck.test(entry.getValue())) {
                toRemovePlayer.add(entry.getKey());
            }
            toRemovePlayer.add(entry.getKey());
        }
        for (K playerId : toRemovePlayer) {
            map.remove(playerId);
        }
    }

}

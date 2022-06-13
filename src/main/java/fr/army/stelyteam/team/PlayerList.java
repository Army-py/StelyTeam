package fr.army.stelyteam.team;

import fr.army.stelyteam.api.IPlayerList;
import fr.army.stelyteam.storage.PlayerTeamTracker;
import fr.army.stelyteam.storage.TeamField;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class PlayerList implements IPlayerList {

    private final Team team;
    private final Lock lock;
    private final Unsafe unsafe;
    private final Map<UUID, Integer> uuids;
    private final PlayerTeamTracker playerTeamTracker;

    public PlayerList(Team team) {
        this(team, null);
    }

    public PlayerList(Team team, Map<UUID, Integer> ids) {
        this.team = team;
        this.lock = new ReentrantLock();
        this.unsafe = new Unsafe();
        this.uuids = ids == null ? new HashMap<>() : new HashMap<>(ids);
        this.playerTeamTracker = new PlayerTeamTracker(lock);
    }

    public Unsafe getUnsafe() {
        return unsafe;
    }

    public Map<UUID, Integer> getUuidRanks() {
        lock.lock();
        try {
            return Collections.unmodifiableMap(uuids);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<UUID> getIds() {
        lock.lock();
        try {
            return Collections.unmodifiableSet(uuids.keySet());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<UUID> getIds(int rank) {
        lock.lock();
        try {
            return this.uuids.entrySet().stream()
                    .filter(e -> e.getValue() == rank)
                    .map(Map.Entry::getKey).collect(Collectors.toUnmodifiableSet());
        } finally {
            lock.unlock();
        }
    }

    public boolean setId(UUID id, int rank) {
        Objects.requireNonNull(id);
        if (rank < 0) {
            throw new IllegalArgumentException("The rank must be superior to 0");
        }
        lock.lock();
        try {
            final Integer previousValue = uuids.put(id, rank);
            // Check if there is changes
            if (previousValue != null && previousValue == rank) {
                return false;
            }
            playerTeamTracker.changeTeam(id, team.getId(), rank);
            team.setDirty(TeamField.PLAYERS);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeId(UUID id) {
        lock.lock();
        try {
            if (uuids.remove(id) != null) {
                playerTeamTracker.changeTeam(id, team.getId(), -1);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            if (uuids.isEmpty()) {
                return;
            }
            for (UUID id : uuids.keySet()) {
                playerTeamTracker.changeTeam(id, team.getId(), -1);
            }
            uuids.clear();
        } finally {
            lock.unlock();
        }
    }

    public PlayerTeamTracker getPlayerTeamTracker() {
        return playerTeamTracker;
    }

    public class Unsafe {

        public void setPlayers(Map<UUID, Integer> players) {
            if (players == null || players.isEmpty()) {
                return;
            }
            lock.lock();
            try {
                PlayerList.this.uuids.putAll(players);
            } finally {
                lock.unlock();
            }
        }

    }

}

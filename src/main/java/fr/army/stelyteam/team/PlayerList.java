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
    private final Map<UUID, Integer> uuids;
    private final PlayerTeamTracker playerTeamTracker;
    private final Lock lock;

    PlayerList(Team team, Map<UUID, Integer> ids) {
        this.team = team;
        this.uuids = ids == null ? new HashMap<>() : new HashMap<>(ids);
        this.lock = new ReentrantLock();
        this.playerTeamTracker = new PlayerTeamTracker(lock);
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
            // Check if the player is added to the team
            if (previousValue == null) {
                playerTeamTracker.changeTeam(id, team.getId());
            }
            // Check if there is no changes
            else if (previousValue == rank) {
                return false;
            }

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
                playerTeamTracker.changeTeam(id, null);
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
                playerTeamTracker.changeTeam(id, null);
            }
            uuids.clear();
        } finally {
            lock.unlock();
        }
    }

    public PlayerTeamTracker getPlayerTeamTracker() {
        return playerTeamTracker;
    }

}

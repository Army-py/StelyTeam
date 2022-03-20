package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;

public class PlayerTeamTracker implements ChangeTracked {

    private final ConcurrentMap<UUID, Optional<Team>> playerTeams;
    private final Lock lock;
    private boolean dirty;

    public PlayerTeamTracker(Lock lock) {
        playerTeams = new ConcurrentHashMap<>();
        this.lock = lock;
    }

    public void changeTeam(UUID playerId, Team teamId) {
        lock.lock();
        try {
            playerTeams.put(playerId, Optional.ofNullable(teamId));
            this.dirty = true;
        } finally {
            lock.unlock();
        }
    }

    public Map<UUID, Optional<Team>> getForSaving() {
        lock.lock();
        try {
            this.dirty = false;
            return new HashMap<>(playerTeams);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isDirty() {
        lock.lock();
        try {
            return dirty;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setDirty(boolean dirty) {
        lock.lock();
        try {
            this.dirty = dirty;
        } finally {
            lock.unlock();
        }
    }
}

package fr.army.stelyteam.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

public class PlayerTeamTracker {

    private final Map<UUID, Optional<UUID>> playerTeams;
    private final Lock lock;
    private boolean dirty;

    public PlayerTeamTracker(Lock lock) {
        playerTeams = new HashMap<>();
        this.lock = lock;
    }

    public void changeTeam(UUID playerId, UUID teamId) {
        lock.lock();
        try {
            playerTeams.put(playerId, Optional.ofNullable(teamId));
            this.dirty = true;
        } finally {
            lock.unlock();
        }
    }

    public Map<UUID, Optional<UUID>> getForSaving() {
        lock.lock();
        try {
            this.dirty = false;
            return new HashMap<>(playerTeams);
        } finally {
            lock.unlock();
        }
    }

    public boolean isDirty() {
        lock.lock();
        try {
            return dirty;
        } finally {
            lock.unlock();
        }
    }

}

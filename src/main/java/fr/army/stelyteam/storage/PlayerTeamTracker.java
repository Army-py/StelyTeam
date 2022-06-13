package fr.army.stelyteam.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

public class PlayerTeamTracker {

    private final Map<UUID, PlayerChange> playerTeams;
    private final Lock lock;
    private boolean dirty;

    public PlayerTeamTracker(Lock lock) {
        playerTeams = new HashMap<>();
        this.lock = lock;
    }

    public void changeTeam(UUID playerId, UUID teamId, int rank) {
        lock.lock();
        try {
            playerTeams.put(playerId, new PlayerChange(teamId, rank));
            this.dirty = true;
        } finally {
            lock.unlock();
        }
    }

    public Map<UUID, PlayerChange> getForSaving() {
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

    public record PlayerChange(UUID teamId, int rank) {

    }

}

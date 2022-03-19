package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerTeamTracker implements ChangeTracked {

    private final ConcurrentMap<UUID, Optional<Team>> playerTeams;
    private final ReentrantLock lock;
    private boolean dirty;

    public PlayerTeamTracker(ConcurrentMap<UUID, Optional<Team>> playerTeams) {
        this.playerTeams = playerTeams;
        lock = new ReentrantLock();
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
        final Map<UUID, Optional<Team>> changes;
        try {
            changes = new HashMap<>(playerTeams);
            this.dirty = false;
        } finally {
            lock.unlock();
        }
        return changes;
    }

    @Override
    public boolean isDirty() {
        return dirty;
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

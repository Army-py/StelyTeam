package fr.army.stelyteam.team;

import fr.army.stelyteam.api.IPlayerList;
import fr.army.stelyteam.storage.PlayerTeamTracker;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerList implements IPlayerList {

    private final Team team;
    private final TeamField teamField;
    private final Set<UUID> uuids;
    private final PlayerTeamTracker playerTeamTracker;
    private final Lock lock;

    public PlayerList(Team team, TeamField teamField) {
        this(team, teamField, null);
    }

    public PlayerList(Team team, TeamField teamField, Set<UUID> ids) {
        this.team = team;
        this.teamField = teamField;
        this.uuids = ids == null ? new HashSet<>() : new HashSet<>(ids);
        this.lock = new ReentrantLock();
        this.playerTeamTracker = new PlayerTeamTracker(lock);
    }

    @Override
    public Set<UUID> getIds() {
        lock.lock();
        try {
            return Collections.unmodifiableSet(uuids);
        } finally {
            lock.unlock();
        }
    }

    public boolean addId(UUID id) {
        lock.lock();
        try {
            if (uuids.add(id)) {
                playerTeamTracker.changeTeam(id, team);
                team.setDirty(teamField);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeId(UUID id) {
        lock.lock();
        try {
            if (uuids.remove(id)) {
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
            for (UUID id : uuids) {
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

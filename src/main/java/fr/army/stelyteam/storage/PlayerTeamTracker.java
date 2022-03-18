package fr.army.stelyteam.storage;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerTeamTracker implements ChangeTracked {

    private final ConcurrentHashMap<UUID, Optional<UUID>> playerTeams;
    private boolean dirty;

    public PlayerTeamTracker(ConcurrentHashMap<UUID, Optional<UUID>> playerTeams) {
        this.playerTeams = playerTeams;
    }

    public void changeTeam(UUID playerId, UUID teamId) {
        this.playerTeams.put(playerId, Optional.ofNullable(teamId));
        setDirty(true);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = true;
    }
}

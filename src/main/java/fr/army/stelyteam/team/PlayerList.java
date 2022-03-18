package fr.army.stelyteam.team;

import fr.army.stelyteam.api.IPlayerList;
import fr.army.stelyteam.storage.ChangeTracked;
import fr.army.stelyteam.storage.PlayerTeamTracker;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class PlayerList implements IPlayerList, ChangeTracked {

    private final UUID teamId;
    private final Set<UUID> uuids;
    private final PlayerTeamTracker playerTeamTracker;
    private boolean dirty;

    public PlayerList(UUID teamId, Set<UUID> uuids, PlayerTeamTracker playerTeamTracker) {
        this.teamId = teamId;
        this.uuids = uuids;
        this.playerTeamTracker = playerTeamTracker;
    }

    @Override
    public Set<UUID> getIds() {
        return Collections.unmodifiableSet(uuids);
    }

    public boolean addId(UUID id) {
        if (uuids.add(id)) {
            playerTeamTracker.changeTeam(id, teamId);
            setDirty(true);
            return true;
        }
        return false;
    }

    public boolean removeId(UUID id) {
        if (uuids.remove(id)) {
            playerTeamTracker.changeTeam(id, null);
            setDirty(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}

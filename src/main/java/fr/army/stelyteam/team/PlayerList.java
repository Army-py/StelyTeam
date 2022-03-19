package fr.army.stelyteam.team;

import fr.army.stelyteam.api.IPlayerList;
import fr.army.stelyteam.storage.PlayerTeamTracker;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class PlayerList implements IPlayerList {

    private final Team team;
    private final Set<UUID> uuids;
    private final PlayerTeamTracker playerTeamTracker;

    public PlayerList(Team team, Set<UUID> uuids, PlayerTeamTracker playerTeamTracker) {
        this.team = team;
        this.uuids = uuids;
        this.playerTeamTracker = playerTeamTracker;
    }

    @Override
    public Set<UUID> getIds() {
        return Collections.unmodifiableSet(uuids);
    }

    public boolean addId(UUID id) {
        if (uuids.add(id)) {
            playerTeamTracker.changeTeam(id, team);
            return true;
        }
        return false;
    }

    public boolean removeId(UUID id) {
        if (uuids.remove(id)) {
            playerTeamTracker.changeTeam(id, null);
            return true;
        }
        return false;
    }

    public void clear() {
        for (UUID id : uuids) {
            playerTeamTracker.changeTeam(id, null);
        }
        uuids.clear();
    }

    public PlayerTeamTracker getPlayerTeamTracker() {
        return playerTeamTracker;
    }

}

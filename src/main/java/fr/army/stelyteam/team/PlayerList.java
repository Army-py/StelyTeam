package fr.army.stelyteam.team;

import fr.army.stelyteam.api.IPlayerList;
import fr.army.stelyteam.storage.ChangeTracked;

import java.util.Set;
import java.util.UUID;

public class PlayerList implements IPlayerList, ChangeTracked {

    private final Set<UUID> uuids;
    private boolean dirty;

    public PlayerList(Set<UUID> uuids) {
        this.uuids = uuids;
    }

    @Override
    public Set<UUID> getIds() {
        return uuids;
    }

    public boolean addId(UUID id) {
        if (uuids.add(id)) {
            setDirty(true);
            return true;
        }
        return false;
    }

    public boolean removeId(UUID id) {
        if (uuids.remove(id)) {
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

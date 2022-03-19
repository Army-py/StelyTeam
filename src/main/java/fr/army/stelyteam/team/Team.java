package fr.army.stelyteam.team;

import fr.army.stelyteam.api.IPlayerList;
import fr.army.stelyteam.api.ITeam;
import fr.army.stelyteam.storage.ChangeTracked;

import java.util.Date;
import java.util.UUID;

public class Team implements ITeam, ChangeTracked {

    private final UUID uuid;
    private final String prefix;
    private final String suffix;
    private final UUID creator;
    private final Date creationDate;
    private final PlayerList owners;
    private final PlayerList members;
    private int dirty;

    public Team(UUID uuid, String prefix, String suffix, UUID creator, Date creationDate, PlayerList owners, PlayerList members) {
        this.uuid = uuid;
        this.prefix = prefix;
        this.suffix = suffix;
        this.creator = creator;
        this.creationDate = creationDate;
        this.owners = owners;
        this.members = members;
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public UUID getCreator() {
        return creator;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public IPlayerList getOwners() {
        return owners;
    }

    @Override
    public IPlayerList getMembers() {
        return members;
    }

    @Override
    public boolean isDirty() {
        return dirty == 0;
    }

    /**
     * Change the dirty flag for every field
     *
     * @param dirty {@code true} to set every field dirty, {@code false} to set every field clean
     */
    @Override
    public void setDirty(boolean dirty) {
        if(dirty) {
            int dirtyValue = 0;
            for (TeamField tf : TeamField.values()) {
                dirtyValue = tf.setDirty(dirtyValue);
            }
        } else {
            this.dirty = 0;
        }
    }
}

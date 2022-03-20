package fr.army.stelyteam.team;

import fr.army.stelyteam.api.ITeam;
import fr.army.stelyteam.storage.ChangeTracked;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Team implements ITeam, ChangeTracked {

    private final UUID uuid;
    private final UUID creator;
    private final Date creationDate;
    private final PlayerList owners;
    private final PlayerList members;
    private final Lock lock;
    private String commandId;
    private String prefix;
    private String suffix;
    private int dirty;

    public Team(UUID uuid, String prefix, String suffix, UUID creator, Date creationDate, PlayerList owners, PlayerList members) {
        this.uuid = uuid;
        this.prefix = prefix;
        this.suffix = suffix;
        this.creator = creator;
        this.creationDate = creationDate;
        this.owners = owners;
        this.members = members;
        this.lock = new ReentrantLock(true);
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        lock.lock();
        try {
            if (Objects.equals(this.commandId, commandId)) {
                return;
            }
            this.commandId = commandId;
            setDirty(TeamField.COMMAND_ID);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        lock.lock();
        try {
            if (Objects.equals(this.prefix, prefix)) {
                return;
            }
            this.prefix = prefix;
            setDirty(TeamField.PREFIX);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        lock.lock();
        try {
            if (Objects.equals(this.suffix, suffix)) {
                return;
            }
            this.suffix = suffix;
            setDirty(TeamField.SUFFIX);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public UUID getCreator() {
        return creator;
    }

    @Override
    public Date getCreationDate() {
        return (Date) creationDate.clone();
    }

    @Override
    public PlayerList getOwners() {
        return owners;
    }

    @Override
    public PlayerList getMembers() {
        return members;
    }

    @Override
    public boolean isDirty() {
        lock.lock();
        try {
            return dirty == 0;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Change the dirty flag for every field
     *
     * @param dirty {@code true} to set every field dirty, {@code false} to set every field clean
     */
    @Override
    public void setDirty(boolean dirty) {
        lock.lock();
        try {
            if (dirty) {
                int dirtyValue = 0;
                for (TeamField tf : TeamField.values()) {
                    dirtyValue = tf.setDirty(dirtyValue);
                }
            } else {
                this.dirty = 0;
            }
        } finally {
            lock.unlock();
        }
    }

    private void setDirty(TeamField teamField) {
        dirty = teamField.setDirty(dirty);
    }

    public Map<TeamField, Optional<Object>> getForSaving() {
        lock.lock();
        try {
            final Map<TeamField, Optional<Object>> changes = new HashMap<>();
            for (TeamField teamField : TeamField.values()) {
                if (!teamField.isDirty(dirty)) {
                    continue;
                }
                final Object fieldValue = switch (teamField) {
                    case ID -> uuid;
                    case COMMAND_ID -> commandId;
                    case PREFIX -> prefix;
                    case SUFFIX -> suffix;
                    case CREATOR -> creator;
                    case CREATION_DATE -> creationDate;
                };
                changes.put(teamField, Optional.ofNullable(fieldValue));
            }

            dirty = 0;
            return changes;
        } finally {
            lock.unlock();
        }
    }

}

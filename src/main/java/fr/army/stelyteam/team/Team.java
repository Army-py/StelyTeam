package fr.army.stelyteam.team;

import fr.army.stelyteam.api.ITeam;
import fr.army.stelyteam.api.LazyLocation;
import fr.army.stelyteam.storage.Storage;
import fr.army.stelyteam.storage.TeamField;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Team implements ITeam {

    private final UUID uuid;
    private final UUID creator;
    private final Date creationDate;
    private final TeamPerks perks;
    private final PlayerList players;
    private final ReentrantLock lock;
    private String commandId;
    private String prefix;
    private String suffix;
    private int dirty;

    Team(
            UUID uuid,
            String commandId,
            String prefix,
            String suffix,
            UUID creator,
            Date creationDate,
            int level,
            boolean bankAccount,
            double money,
            LazyLocation home,
            Map<UUID, Integer> players
    ) {
        Objects.requireNonNull(uuid);
        this.uuid = uuid;
        this.commandId = commandId;
        this.prefix = prefix;
        this.suffix = suffix;
        this.creator = creator;
        this.creationDate = creationDate == null ? null : (Date) creationDate.clone();
        this.lock = new ReentrantLock(true);
        this.perks = new TeamPerks(
                this,
                lock,
                level,
                bankAccount,
                money,
                home
        );
        this.players = new PlayerList(this, players);

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
        return creationDate == null ? null : (Date) creationDate.clone();
    }

    @Override
    public TeamPerks getPerks() {
        return perks;
    }


    @Override
    public PlayerList getPlayers() {
        return players;
    }

    public boolean isDirty() {
        lock.lock();
        try {
            return dirty != 0;
        } finally {
            lock.unlock();
        }
    }

    public void setDirty(TeamField teamField) {
        lock.lock();
        try {
            dirty = teamField.setDirty(dirty);
        } finally {
            lock.unlock();
        }
    }

    public List<Storage.FieldValues> getForSaving() {
        lock.lock();
        try {
            final List<Storage.FieldValues> changes = new LinkedList<>();
            for (TeamField teamField : TeamField.values()) {
                if (!teamField.isDirty(dirty)) {
                    continue;
                }

                changes.add(new Storage.FieldValues(
                        teamField,
                        Optional.ofNullable(teamField.getExtractor().extract(this))
                ));
            }

            dirty = 0;
            return changes;
        } finally {
            lock.unlock();
        }
    }

}

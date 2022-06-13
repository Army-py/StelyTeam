package fr.army.stelyteam.team;

import fr.army.stelyteam.api.ITeam;
import fr.army.stelyteam.api.LazyLocation;
import fr.army.stelyteam.storage.Storage;
import fr.army.stelyteam.storage.TeamField;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Team implements ITeam {

    private final Unsafe unsafe;
    private final ReentrantLock lock;
    private final UUID uuid;
    private final TeamPerks perks;
    private final PlayerList players;
    private UUID creator;
    private Date creationDate;
    private String commandId;
    private String prefix;
    private String suffix;
    private int dirty;

    public Team(UUID uuid) {
        Objects.requireNonNull(uuid);
        this.uuid = uuid;
        unsafe = new Unsafe();
        lock = new ReentrantLock(true);
        perks = new TeamPerks(this, lock);
        players = new PlayerList(this);
    }

    public Team(
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
        this.unsafe = new Unsafe();
        this.lock = new ReentrantLock(true);
        this.uuid = uuid;
        this.commandId = commandId;
        this.prefix = prefix;
        this.suffix = suffix;
        this.creator = creator;
        this.creationDate = creationDate == null ? null : (Date) creationDate.clone();
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

    public Unsafe getUnsafe() {
        return unsafe;
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
            if (unsafe.setCommandId(commandId)) {
                setDirty(TeamField.COMMAND_ID);
            }
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
            if (unsafe.setPrefix(prefix)) {
                setDirty(TeamField.PREFIX);
            }
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
            if (unsafe.setSuffix(suffix)) {
                setDirty(TeamField.SUFFIX);
            }
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

    public class Unsafe {

        public boolean setCommandId(String commandId) {
            lock.lock();
            try {
                if (Objects.equals(Team.this.commandId, commandId)) {
                    return false;
                }
                Team.this.commandId = commandId;
                return true;
            } finally {
                lock.unlock();
            }
        }

        public boolean setPrefix(String prefix) {
            lock.lock();
            try {
                if (Objects.equals(Team.this.prefix, prefix)) {
                    return false;
                }
                Team.this.prefix = prefix;
                return true;
            } finally {
                lock.unlock();
            }
        }

        public boolean setSuffix(String suffix) {
            lock.lock();
            try {
                if (Objects.equals(Team.this.suffix, suffix)) {
                    return false;
                }
                Team.this.suffix = suffix;
                return true;
            } finally {
                lock.unlock();
            }
        }

        public void setCreator(UUID creator) {
            Team.this.creator = creator;
        }

        public void setCreationDate(Date creationDate) {
            Team.this.creationDate = creationDate;
        }

    }

}

package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Property<T> {

    private final TeamField teamField;
    private final Lock lock;
    private T value;
    private boolean dirty;
    private boolean loaded;

    public Property(@NotNull TeamField teamField) {
        this.teamField = teamField;
        this.lock = new ReentrantLock();
        dirty = false;
        loaded = false;
    }

    public TeamField getTeamField() {
        return teamField;
    }

    @Nullable
    public T get() {
        try {
            lock.lock();
            return value;
        } finally {
            lock.unlock();
        }
    }

    public boolean set(@Nullable T value) {
        try {
            lock.lock();
            if (Objects.equals(value, this.value)) {
                return false;
            }
            this.value = value;
            dirty = true;
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Load a value in this property
     * It should only be used internally
     *
     * @param value The value of the property
     */
    public void loadUnsafe(@Nullable T value) {
        try {
            lock.lock();
            this.value = value;
            dirty = false;
            loaded = true;
        } finally {
            lock.unlock();
        }
    }

    @Nullable
    public Property<T> retrieve(@NotNull UUID teamId, @NotNull StorageManager storageManager) {
        try {
            lock.lock();
            if (loaded) {
                return this;
            }
            storageManager.retrieve(teamId, this);
            return this;
        } finally {
            lock.unlock();
        }
    }

    public boolean save(@NotNull UUID teamId, @NotNull StorageManager storageManager) {
        try {
            lock.lock();
            if(!dirty) {
                return false;
            }
            storageManager.save(teamId, this);
            dirty = false;
            loaded = true;
            return true;
        } finally {
            lock.unlock();
        }
    }

}

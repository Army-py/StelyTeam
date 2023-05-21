package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.locks.Lock;

public class Property<T> {

    private final Lock lock;
    private final Storage storage;
    private T value;
    private boolean dirty;
    private boolean loaded;

    public Property(@NotNull Lock lock, @NotNull Storage storage) {
        this.lock = lock;
        this.storage = storage;
        dirty = false;
        loaded = false;
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
    public T retrieve() {
        try {
            lock.lock();
            if(loaded) {
                return value;
            }
            loadUnsafe(storage.retrieve(this));
            return value;
        } finally {
            lock.unlock();
        }
    }

    public boolean save() {
        try {
            lock.lock();
            if(!dirty) {
                return false;
            }
            // TODO Save
            return true;
        } finally {
            lock.unlock();
        }
    }

}

package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Property<T> implements IProperty {

    private final SaveField saveField;
    private final Lock lock;
    private T value;
    private boolean dirty;
    private boolean loaded;

    public Property(@NotNull SaveField saveField) {
        this.saveField = saveField;
        this.lock = new ReentrantLock();
        dirty = false;
        loaded = false;
    }

    @NotNull
    @Override
    public SaveField getField() {
        return saveField;
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

    @Override
    public boolean isLoaded() {
        try {
            lock.lock();
            return loaded;
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

    public boolean save(@NotNull PropertiesHolder holder, @NotNull List<SaveProperty<?>> saveValues) {
        try {
            lock.lock();
            if (!dirty) {
                return false;
            }
            saveValues.add(new SaveValue<>(saveField, holder, value));
            dirty = false;
            loaded = true;
            return true;
        } finally {
            lock.unlock();
        }
    }

}

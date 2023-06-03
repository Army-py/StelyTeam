package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SetProperty<T extends PropertiesHolder> implements IProperty {

    private final SaveField saveField;
    private final Lock lock;
    private final Set<T> values;
    private Map<T, Boolean> changes;
    private boolean loaded;

    public SetProperty(@NotNull SaveField saveField) {
        this.saveField = saveField;
        this.lock = new ReentrantLock();
        this.values = new HashSet<>();
        this.changes = new HashMap<>();
    }

    @Override
    public @NotNull SaveField getField() {
        return saveField;
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

    /**
     * Load a value in this property
     * It should only be used internally
     *
     * @param values The value of the property
     */
    public void loadUnsafe(@Nullable Collection<T> values) {
        setUnsafe(() -> {
            this.values.clear();
            if (values != null) {
                this.values.addAll(values);
            }
        });
    }

    public void addUnsafe(@NotNull Collection<T> values) {
        setUnsafe(() -> this.values.addAll(values));
    }

    public void removeUnsafe(@NotNull Collection<T> values) {
        setUnsafe(() -> this.values.removeAll(values));
    }

    private void setUnsafe(@NotNull Runnable action) {
        try {
            lock.lock();
            action.run();
            changes = new HashMap<>();
            loaded = true;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        try {
            lock.lock();
            return values.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        try {
            lock.lock();
            return values.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(T value) {
        try {
            lock.lock();
            return values.contains(value);
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NotNull
    public Object @NotNull [] toArray() {
        try {
            lock.lock();
            return values.toArray();
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    public T @NotNull [] toArray(T @NotNull [] a) {
        try {
            lock.lock();
            return values.toArray(a);
        } finally {
            lock.unlock();
        }
    }

    private void computeChange(@NotNull T value, boolean val) {
        this.changes.merge(value, val, (k, v) -> null);
    }

    public boolean add(@NotNull T o) {
        try {
            lock.lock();
            final boolean changed = values.add(o);
            if (changed) {
                computeChange(o, true);
            }
            return changed;
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(@NotNull T o) {
        try {
            lock.lock();
            final boolean changed = values.remove(o);
            if (changed) {
                computeChange(o, false);
            }
            return changed;
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        try {
            lock.lock();
            if (values.isEmpty()) {
                return;
            }
            values.clear();
            changes = new HashMap<>();
        } finally {
            lock.unlock();
        }
    }


}

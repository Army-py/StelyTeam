package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SetProperty<T> implements IProperty, Set<T> {

    private final TeamField teamField;
    private final Lock lock;
    private final Set<T> set;
    private boolean loaded;
    private boolean dirty;

    public SetProperty(@NotNull TeamField teamField) {
        this.teamField = teamField;
        this.lock = new ReentrantLock();
        this.set = new HashSet<>();
    }

    @Override
    public @NotNull TeamField getField() {
        return teamField;
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
            set.clear();
            if (values != null) {
                set.addAll(values);
            }
        });
    }

    public void addUnsafe(@NotNull Collection<T> values) {
        setUnsafe(() -> set.addAll(values));
    }

    public void removeUnsafe(@NotNull Collection<T> values) {
        setUnsafe(() -> set.removeAll(values));
    }

    private void setUnsafe(@NotNull Runnable action) {
        try {
            lock.lock();
            action.run();
            dirty = false;
            loaded = true;
        } finally {
            lock.unlock();
        }
    }

    public SetProperty<T> retrieve(@NotNull UUID teamId, @NotNull StorageManager storageManager) {
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

    @Override
    public int size() {
        try {
            lock.lock();
            return set.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            lock.lock();
            return set.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(Object o) {
        try {
            lock.lock();
            return set.contains(o);
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        try {
            lock.lock();
            return set.toArray();
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    @Override
    public <T1> T1 @NotNull [] toArray(T1 @NotNull [] a) {
        try {
            lock.lock();
            return set.toArray(a);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean add(T o) {
        try {
            lock.lock();
            final boolean changed = set.add(o);
            if(changed) {
                dirty = true;
            }
            return changed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object o) {
        try {
            lock.lock();
            final boolean changed = set.remove(o);
            if(changed) {
                dirty = true;
            }
            return changed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        try {
            lock.lock();
            final boolean changed = set.addAll(c);
            if(changed) {
                dirty = true;
            }
            return changed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.lock();
            if(set.isEmpty()) {
                return;
            }
            set.clear();
            dirty = true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        try {
            lock.lock();
            final boolean changed = set.removeAll(c);
            if(changed) {
                dirty = true;
            }
            return changed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        try {
            lock.lock();
            final boolean changed = set.retainAll(c);
            if(changed) {
                dirty = true;
            }
            return changed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        try {
            lock.lock();
            return set.containsAll(c);
        } finally {
            lock.unlock();
        }
    }


}

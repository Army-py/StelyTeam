package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class SetProperty<I, T> implements IProperty {

    private final SaveField saveField;
    private final Lock lock;
    private final Function<T, I> identifierMapper;
    private final Map<I, T> values;
    private Map<I, ChangeValue<T>> changes;
    private boolean loaded;

    private record ChangeValue<T>(@NotNull T value, boolean changeValue) {
    }

    public SetProperty(@NotNull SaveField saveField, @NotNull Function<T, I> identifierMapper) {
        this.saveField = saveField;
        this.lock = new ReentrantLock();
        this.identifierMapper = identifierMapper;
        this.values = new HashMap<>();
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
    public <O> void loadUnsafe(@Nullable Collection<O> values, @NotNull Function<O, T> constructor) {
        setUnsafe(() -> {
            this.values.clear();
            if (values == null) {
                return;
            }
            for (O value : values) {
                if (value == null) {
                    continue;
                }
                final T typedValue = constructor.apply(value);
                this.values.put(identifierMapper.apply(typedValue), typedValue);
            }
        });
    }

    public void addUnsafe(@NotNull Collection<T> values) {
        setUnsafe(() -> {
            for (T value : values) {
                if (value == null) {
                    continue;
                }
                this.values.put(identifierMapper.apply(value), value);
            }
        });
    }

    public void removeUnsafe(@NotNull Collection<T> values) {
        setUnsafe(() -> {
            for (T value : values) {
                if (value == null) {
                    continue;
                }
                this.values.remove(identifierMapper.apply(value));
            }
        });
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

    public T getValue(@NotNull I identifier) {
        try {
            lock.lock();
            return values.get(identifier);
        } finally {
            lock.unlock();
        }
    }

    public boolean save(@NotNull PropertiesHolder holder, @NotNull List<SaveProperty<?>> saveValues, @NotNull T[] a) {
        try {
            lock.lock();
            if (changes.isEmpty()) {
                return false;
            }
            // Save the set itself
            final List<T> added = new LinkedList<>();
            final List<T> removed = new LinkedList<>();
            for (Map.Entry<I, ChangeValue<T>> entry : changes.entrySet()) {
                if (entry.getValue().changeValue()) {
                    added.add(entry.getValue().value());
                    continue;
                }
                removed.add(entry.getValue().value());
            }
            final T[] values = this.values.values().toArray(a);
            saveValues.add(new SaveSet<>(saveField, holder, added.toArray(a), removed.toArray(a), values));
            // Save its elements values
            if (a.getClass().getComponentType().isAssignableFrom(PropertiesHolder.class)) {
                for (T value : values) {
                    ((PropertiesHolder) value).save(saveValues);
                }
            }
            changes = new HashMap<>();
            loaded = true;
            return true;
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

    public boolean contains(I identifier) {
        try {
            lock.lock();
            return values.containsKey(identifier);
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @NotNull
    public T @NotNull [] toArray(T @NotNull [] a) {
        try {
            lock.lock();
            return values.values().toArray(a);
        } finally {
            lock.unlock();
        }
    }

    private void computeChange(@NotNull I id, @NotNull T value, boolean val) {
        this.changes.merge(id, new ChangeValue<>(value, val), (k, v) -> null);
    }

    public boolean add(@NotNull T o) {
        try {
            lock.lock();
            final I id = identifierMapper.apply(o);
            final T previousValue = values.put(id, o);
            final boolean changed = previousValue != null;
            if (changed) {
                computeChange(id, o, true);
            }
            return changed;
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(@NotNull I id) {
        try {
            lock.lock();
            final T previousValue = values.remove(id);
            final boolean changed = previousValue != null;
            if (changed) {
                computeChange(id, previousValue, false);
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

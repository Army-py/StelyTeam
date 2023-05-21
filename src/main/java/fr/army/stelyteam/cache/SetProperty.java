package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public class SetProperty<T> implements Set<T> {

    private final Lock lock;
    private final Set<T> set;
    private boolean dirty;

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
    public <T1> T1 @NotNull [] toArray(@NotNull T1[] a) {
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

package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;

public class SaveSet<T> extends SaveProperty<T> {

    private final T[] added;
    private final T[] removed;
    private final T[] values;

    public SaveSet(@NotNull SaveField field, @NotNull PropertiesHolder holder, @NotNull T[] added, @NotNull T[] removed, @NotNull T[] values) {
        super(field, holder);
        this.added = added;
        this.removed = removed;
        this.values = values;
    }

    @Override
    public @NotNull SaveSet<T> asSet() {
        return this;
    }

    @NotNull
    public T[] getAdded() {
        return added;
    }

    @NotNull
    public T[] getRemoved() {
        return removed;
    }

    @NotNull
    public T[] getValues() {
        return values;
    }

}

package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;

public abstract class SaveProperty<T> {

    private final SaveField field;
    private final PropertiesHolder holder;

    public SaveProperty(@NotNull SaveField field, @NotNull PropertiesHolder holder) {
        this.field = field;
        this.holder = holder;
    }

    @NotNull
    public SaveField getField() {
        return field;
    }

    @NotNull
    public PropertiesHolder getHolder() {
        return holder;
    }

    @NotNull
    public SaveValue<T> asValue() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public SaveSet<T> asSet() {
        throw new UnsupportedOperationException();
    }

}

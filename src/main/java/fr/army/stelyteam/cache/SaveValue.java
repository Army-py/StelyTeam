package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;

public class SaveValue<T> extends SaveProperty<T> {

    private final T value;

    public SaveValue(@NotNull SaveField field, @NotNull PropertiesHolder holder, @NotNull T value) {
        super(field, holder);
        this.value = value;
    }

    @Override
    public @NotNull SaveValue<T> asValue() {
        return this;
    }

    @NotNull
    public T getValue() {
        return value;
    }

}

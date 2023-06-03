package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;

public record SaveValue<T>(@NotNull SaveField field, T value) {
}

package fr.army.stelyteam.cache;

import org.jetbrains.annotations.NotNull;

public record SaveValue<T>(@NotNull TeamField field, T value) {
}

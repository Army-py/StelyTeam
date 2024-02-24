package fr.army.stelyteam.utils.network.order;

import org.jetbrains.annotations.NotNull;

public record Order(@NotNull String sourceServerName, @NotNull byte[] data) {
}

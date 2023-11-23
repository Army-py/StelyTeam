package fr.army.stelyteam.team;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record BankAccountSnapShot(
        @NotNull Optional<Boolean> unlocked,
        @NotNull Optional<Double> balance
) {
}

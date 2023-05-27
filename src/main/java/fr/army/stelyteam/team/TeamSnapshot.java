package fr.army.stelyteam.team;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public record TeamSnapshot(
        @NotNull UUID uuid,
        @NotNull Optional<String> name,
        @NotNull Optional<String> prefix,
        @NotNull Optional<String> description,
        @NotNull Optional<Date> creationDate,
        @NotNull Optional<BankAccountSnapShot> bankAccount,
        @NotNull Optional<String> owner
) {

}

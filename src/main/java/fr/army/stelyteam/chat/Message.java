package fr.army.stelyteam.chat;

import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public record Message(@NotNull UUID senderUuid, @NotNull String messageFormat, @NotNull Set<UUID> recipients){
}

package fr.army.stelyteam.cache;

import fr.army.stelyteam.team.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StorageManager {

    private final Storage storage;

    @Nullable
    public Team retreiveTeam(@NotNull String teamName, TeamField... fields) {
        // TODO Fetch the team from the current storage
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Nullable
    public Team retreivePlayerTeam(@NotNull String playerName, TeamField... fields) {
        // TODO Fetch the team from the current storage
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void retrieve(@NotNull UUID teamId, @NotNull Property<?>... properties) {
        // TODO Fetch team information based on the specified properties from the current storage
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public <T> void retrieve(UUID teamId, @NotNull SetProperty<?>... properties) {
        // TODO Fetch team informations base on the specified properties from the current storage
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void save(@NotNull UUID teamId, @NotNull Property<?>... properties) {
        // TODO Save team properties
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void remove(UUID teamId) {
        // TODO Remove the team from the database
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

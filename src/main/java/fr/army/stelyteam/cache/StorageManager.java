package fr.army.stelyteam.cache;

import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StorageManager {

    private final Storage storage;

    @Nullable
    public TeamSnapshot retrieveTeam(@NotNull String teamName, @NotNull SaveField... fields) {
        // TODO Fetch the team from the current storage
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Nullable
    public Team retrievePlayerTeam(@NotNull String playerName, @NotNull SaveField... fields) {
        // TODO Fetch the team from the current storage
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public TeamSnapshot retrieveTeam(@NotNull UUID teamId, @NotNull SaveField... fields) {
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

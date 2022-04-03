package fr.army.stelyteam.storage.file;

import fr.army.stelyteam.storage.Storage;
import fr.army.stelyteam.team.TeamField;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FileStorage implements Storage {

    @Override
    public CompletableFuture<Map<TeamField, Optional<Object>>> loadTeam(UUID teamID, TeamField[] teamField) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<Optional<UUID>> getTeamId(String commandId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<Void> saveTeam(UUID teamId, List<FieldValues> changes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<Void> deleteTeam(UUID teamId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<Optional<UUID>> getPlayerTeamId(UUID playerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<Void> savePlayerTeams(Map<UUID, Optional<UUID>> changes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

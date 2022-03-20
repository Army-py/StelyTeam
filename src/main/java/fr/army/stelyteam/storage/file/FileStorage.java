package fr.army.stelyteam.storage.file;

import fr.army.stelyteam.storage.Storage;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamField;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FileStorage implements Storage {

    @Override
    public CompletableFuture<Team> loadTeam(UUID teamID) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<Void> saveTeam(Map<TeamField, Optional<Object>> team) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<Void> deleteTeam(UUID teamId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<UUID> getPlayerTeamId(UUID playerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<Void> savePlayerTeams(Map<UUID, Optional<Team>> changes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

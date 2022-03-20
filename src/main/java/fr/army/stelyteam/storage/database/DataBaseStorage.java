package fr.army.stelyteam.storage.database;

import fr.army.stelyteam.storage.PlayerTeamTracker;
import fr.army.stelyteam.storage.Storage;
import fr.army.stelyteam.team.Team;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DataBaseStorage implements Storage {

    //TODO Implement this class with the current database system

    @Override
    public CompletableFuture<Team> loadTeam(UUID teamID) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<Void> saveTeam(Team team) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<UUID> deleteTeam(UUID teamId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<UUID> getPlayerTeamId(UUID playerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CompletableFuture<Map<UUID, Optional<Team>>> savePlayerTeams(PlayerTeamTracker playerTeamTracker) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}

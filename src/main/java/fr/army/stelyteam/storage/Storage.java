package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamField;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    CompletableFuture<Map<TeamField, Optional<Object>>> loadTeam(UUID teamID, TeamField[] teamFields);

    CompletableFuture<Optional<UUID>> getTeamId(String commandId);

    CompletableFuture<Void> saveTeam(UUID teamId, List<FieldValues> changes);

    CompletableFuture<Void> deleteTeam(UUID teamId);

    CompletableFuture<Optional<UUID>> getPlayerTeamId(UUID playerId);

    CompletableFuture<Void> savePlayerTeams(Map<UUID, Optional<Team>> changes);

    record FieldValues(TeamField field, Optional<Object> value) {
    }

}

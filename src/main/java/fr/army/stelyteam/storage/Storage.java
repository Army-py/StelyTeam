package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamField;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    CompletableFuture<Map<TeamField, Optional<Object>>> loadTeam(UUID teamID, Set<TeamField> teamField);

    CompletableFuture<Void> saveTeam(UUID teamId, Map<TeamField, Optional<Object>> changes);

    CompletableFuture<Void> deleteTeam(UUID teamId);

    CompletableFuture<UUID> getPlayerTeamId(UUID playerId);

    CompletableFuture<Void> savePlayerTeams(Map<UUID, Optional<Team>> changes);


}

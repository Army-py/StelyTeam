package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamField;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    CompletableFuture<Team> loadTeam(UUID teamID);

    CompletableFuture<Void> saveTeam(Map<TeamField, Optional<Object>> team);

    CompletableFuture<Void> deleteTeam(UUID teamId);

    CompletableFuture<UUID> getPlayerTeamId(UUID playerId);

    CompletableFuture<Void> savePlayerTeams(Map<UUID, Optional<Team>> changes);


}

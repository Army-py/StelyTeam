package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    CompletableFuture<Team> loadTeam(UUID teamID);

    CompletableFuture<Void> saveTeam(Team team);

    CompletableFuture<UUID> deleteTeam(UUID teamId);

    CompletableFuture<UUID> getPlayerTeamId(UUID playerId);

    CompletableFuture<Map<UUID, Optional<Team>>> savePlayerTeams(PlayerTeamTracker playerTeamTracker);


}

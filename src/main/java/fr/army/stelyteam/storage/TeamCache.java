package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

public class TeamCache {

    private final TeamManager teamManager;
    private final ConcurrentMap<UUID, UUID> playerTeams;
    private final ConcurrentMap<UUID, Team> teamById;

    public TeamCache(TeamManager teamManager, ConcurrentMap<UUID, UUID> playerTeams, ConcurrentMap<UUID, Team> teamById) {
        this.teamManager = teamManager;
        this.playerTeams = playerTeams;
        this.teamById = teamById;
    }

    /**
     * Load a {@link Player} in the {@link TeamManager}'s cache
     *
     * @param playerId The {@link UUID} of the player to load
     * @return A {@link CompletableFuture} that symbolize the load action
     */
    public CompletableFuture<Void> loadPlayer(UUID playerId) {
        // Unload previous cache to prevent wasted memory
        unloadPlayer(playerId);

        // Load the team from the storage
        final CompletableFuture<Optional<Team>> teamFuture = teamManager.getOrLoadPlayerTeam(playerId);
        // Fill the cache
        return teamFuture.thenAccept(optionalTeam -> {
            if (optionalTeam.isPresent()) {
                final Team team = optionalTeam.get();
                teamById.put(team.getId(), team);
                playerTeams.put(playerId, team.getId());
            }
        });
    }

    /**
     * Unload a {@link Player} in the {@link TeamManager}'s cache
     *
     * @param playerId The {@link UUID} of the player to unload
     * @return {@code true} if the player was in the cache. {@code false} otherwise
     */
    public boolean unloadPlayer(UUID playerId) {
        // Unset the player team mapping
        final UUID teamId = playerTeams.remove(playerId);
        if (teamId == null) {
            return false;
        }
        // Check if the team must be removed from the cache
        if (!playerTeams.containsValue(teamId)) {
            teamById.remove(teamId);
        }
        return true;
    }

}

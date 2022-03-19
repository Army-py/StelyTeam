package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.PlayerList;
import fr.army.stelyteam.team.Team;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TeamManager {

    private final Storage storage;
    private final ConcurrentMap<UUID, Team> playerTeams;
    private final ConcurrentMap<UUID, Team> teamById;

    public TeamManager(Storage storage) {
        this.storage = storage;
        playerTeams = new ConcurrentHashMap<>();
        teamById = new ConcurrentHashMap<>();
    }

    /**
     * Get a {@link Team} from the cache.
     * It cans return {@code null} if the {@link Team} with the given id is not loaded.
     *
     * @param id The {@link Team}'s id
     * @return The {@link Team} from the cache that have the id {@code id}.
     * {@code null} if the {@link Team} does not exist or is not loaded
     */
    public Team getLoadedTeam(UUID id) {
        return teamById.get(id);
    }

    /**
     * Get a {@link Team} from the cache or the storage
     *
     * @param id The {@link Team}'s id
     * @return A {@link CompletableFuture} that return the {@link Team} that have the id {@code id}.
     * The {@link CompletableFuture} return {@code null} if the {@link Team} does not exist
     */
    public CompletableFuture<Team> getOrLoadTeam(UUID id) {
        final Team cached = getLoadedTeam(id);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        return storage.loadTeam(id);
    }

    /**
     * Load the player {@link Team} from the cache
     *
     * @param playerId The player {@link UUID}
     * @return The {@link Team} of the player.
     * {@code null} if the player does not have a {@link Team} or if he's not loaded
     */
    public Team getPlayerTeam(UUID playerId) {
        return playerTeams.get(playerId);
    }

    /**
     * Get the player {@link Team}
     *
     * @param playerId The player {@link UUID}
     * @return A {@link CompletableFuture} that return the {@link Team} of the player
     * The {@link CompletableFuture} return {@code null} if the player does not have a {@link Team}
     */
    public CompletableFuture<Team> getOrLoadPlayerTeam(UUID playerId) {
        final Team cachedTeam = getPlayerTeam(playerId);
        if (cachedTeam != null) {
            return CompletableFuture.completedFuture(cachedTeam);
        }

        return storage.getPlayerTeamId(playerId).thenCompose(storage::loadTeam);
    }

    /**
     * Load the {@link UUID} of the team of a player
     *
     * @param playerId The player {@link UUID}
     * @return The {@link Team} of the player.
     * {@code null} if the player does not have a {@link Team}
     */
    public CompletableFuture<UUID> getPlayerTeamId(UUID playerId) {
        final Team cachedTeam = getPlayerTeam(playerId);
        if (cachedTeam != null) {
            return CompletableFuture.completedFuture(cachedTeam.getId());
        }

        return storage.getPlayerTeamId(playerId);
    }

    /**
     * Save a {@link Team} in the storage
     *
     * @param team The {@link Team} to save
     * @return A {@link CompletableFuture} representing the save action
     */
    public CompletableFuture<Void> save(Team team) {
        return null;
    }

    /**
     * Delete permanently a {@link Team} from the storage
     *
     * @param team The {@link Team} to delete
     */
    public CompletableFuture<Void> delete(Team team) {
        // Kick the players from the team
        final PlayerList owners = team.getOwners();
        final PlayerList members = team.getMembers();
        owners.clear();
        members.clear();

        // Delete it from storage and then delete it from the cache
        return CompletableFuture.allOf(
                storage.deleteTeam(team.getId()).thenAccept(teamById::remove),
                storage.savePlayerTeams(owners.getPlayerTeamTracker()).thenAccept(this::linkPlayerTeams),
                storage.savePlayerTeams(members.getPlayerTeamTracker()).thenAccept(this::linkPlayerTeams)
        );
    }

    /**
     * Link players to their {@link Team}
     *
     * @param map The {@link Map} containing the mapping between players and teams
     */
    private void linkPlayerTeams(Map<UUID, Optional<Team>> map) {
        for (Map.Entry<UUID, Optional<Team>> entry : map.entrySet()) {
            final Optional<Team> value = entry.getValue();
            if (value.isEmpty()) {
                playerTeams.remove(entry.getKey());
            } else {
                playerTeams.put(entry.getKey(), value.get());
            }
        }
    }


}

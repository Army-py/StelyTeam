package fr.army.stelyteam.storage;

import fr.army.stelyteam.storage.network.NetworkManager;
import fr.army.stelyteam.team.PlayerList;
import fr.army.stelyteam.team.Team;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class TeamManager {

    private final NetworkManager networkManager;
    private final StorageManager storageManager;
    private final ConcurrentMap<UUID, UUID> playerTeams;
    private final ConcurrentMap<UUID, Team> teamById;
    private final TeamCache teamCache;
    private final ReentrantLock lock;

    public TeamManager(NetworkManager networkManager, StorageManager storageManager) {
        this.networkManager = networkManager;
        this.storageManager = storageManager;
        playerTeams = new ConcurrentHashMap<>();
        teamById = new ConcurrentHashMap<>();
        teamCache = new TeamCache(this, playerTeams, teamById);
        lock = new ReentrantLock();
    }

    /**
     * Get the {@link TeamCache} of this {@link TeamManager}
     *
     * @return The {@link TeamCache} instance used by this {@link TeamManager}
     */
    public TeamCache getTeamCache() {
        return teamCache;
    }

    /**
     * Get an {@link Optional} {@link Team} from the cache.
     *
     * @param id The {@link Team}'s id
     * @return The {@link Team} from the cache that have the id {@code id}.
     * An empty {@link Optional} if the {@link Team} does not exist or is not loaded
     */
    public Optional<Team> getLoadedTeam(UUID id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable(teamById.get(id));
    }

    /**
     * Get an {@link Optional} {@link Team} from the cache.
     *
     * @param commandId The {@link Team}'s command id
     * @return An {@link Optional} {@link Team} from the cache that have the command id {@code commandId}.
     * An empty {@link Optional} if the {@link Team} does not exist or is not loaded
     */
    public Optional<Team> getLoadedTeam(String commandId) {
        Objects.requireNonNull(commandId);
        return teamById.values().stream().filter(team -> commandId.equals(team.getCommandId())).findAny();
    }

    /**
     * Get an {@link Optional} {@link Team} from the cache or the storage
     *
     * @param id The {@link Team}'s id
     * @return A {@link CompletableFuture} that return an {@link Optional} {@link Team} that have the id {@code id}.
     * The {@link CompletableFuture} return an empty {@link Optional} if the {@link Team} does not exist
     */
    public CompletableFuture<Optional<Team>> getOrLoadTeam(UUID id) {
        Objects.requireNonNull(id);
        final Optional<Team> cached = getLoadedTeam(id);
        if (cached.isPresent()) {
            return CompletableFuture.completedFuture(cached);
        }
        return storageManager.loadTeam(id);
    }

    /**
     * Get an {@link Optional} {@link Team}'s {@link UUID} from the cache or the storage
     *
     * @param commandId The {@link Team}'s command id
     * @return A {@link CompletableFuture} that return an {@link Optional} {@link UUID} of the {@link Team }
     * that have the command id {@code commandId}.
     * The {@link CompletableFuture} return an empty {@link Optional} if the {@link Team} does not exist
     */
    public CompletableFuture<Optional<UUID>> getOrLoadTeam(String commandId) {
        Objects.requireNonNull(commandId);
        final Optional<Team> cached = getLoadedTeam(commandId);
        return cached
                .map(team -> CompletableFuture.completedFuture(Optional.of(team.getId())))
                .orElseGet(() -> storageManager.getTeamId(commandId));
    }

    /**
     * Get an {@link Optional} {@link Team}'s {@link UUID} from the cache
     *
     * @param playerId The player {@link UUID}
     * @return An {@link Optional} {@link UUID}.
     * The {@link Optional} is empty if the {@link Team} is not loaded or the player does not have a {@link Team}
     */
    public Optional<UUID> getLoadedPlayerTeamId(UUID playerId) {
        Objects.requireNonNull(playerId);
        return Optional.ofNullable(playerTeams.get(playerId));
    }

    /**
     * Load the player's {@link Team} from the cache
     *
     * @param playerId The player {@link UUID}
     * @return An {@link Optional} {@link Team}.
     * The {@link Optional} is empty if the {@link Team} is not loaded or the player does not have a {@link Team}
     */
    public Optional<Team> getLoadedPlayerTeam(UUID playerId) {
        Objects.requireNonNull(playerId);
        return getLoadedPlayerTeamId(playerId).map(teamById::get);
    }

    /**
     * Get the player {@link Team}'s {@link UUID} from the cache or the permanent storage
     *
     * @param playerId The player {@link UUID}
     * @return A {@link CompletableFuture} that return the {@link Optional} {@link Team}'s {@link UUID} of the player
     * The {@link CompletableFuture} return an empty {@link Optional} if the player does not have a {@link Team}
     */
    public CompletableFuture<Optional<UUID>> getOrLoadPlayerTeamId(UUID playerId) {
        Objects.requireNonNull(playerId);
        final Optional<UUID> teamId = getLoadedPlayerTeamId(playerId);
        if (teamId.isPresent()) {
            return CompletableFuture.completedFuture(teamId);
        }
        return storageManager.getPlayerTeamId(playerId);
    }

    /**
     * Get the player {@link Team}
     *
     * @param playerId The player {@link UUID}
     * @return A {@link CompletableFuture} that return the {@link Optional} {@link Team} of the player
     * The {@link CompletableFuture} return an empty {@link Optional} if the player does not have a {@link Team}
     */
    public CompletableFuture<Optional<Team>> getOrLoadPlayerTeam(UUID playerId) {
        Objects.requireNonNull(playerId);
        return getOrLoadPlayerTeamId(playerId).thenCompose(teamId -> {
            if (teamId.isPresent()) {
                return getOrLoadTeam(teamId.get());
            }
            return CompletableFuture.completedFuture(Optional.empty());
        });
    }

    /**
     * Save a {@link Team} in the storage
     *
     * @param team The {@link Team} to save
     * @return A {@link CompletableFuture} representing the save action
     */
    public CompletableFuture<Void> save(Team team) {
        Objects.requireNonNull(team);
        lock.lock();
        try {
            final List<CompletableFuture<?>> futures = new LinkedList<>();
            // Save team
            if (team.isDirty()) {
                final List<Storage.FieldValues> fieldValues = team.getForSaving();
                networkManager.sendSave(fieldValues);
                futures.add(storageManager.saveTeam(team.getId(), fieldValues));
            }

            // Save players in the team
            savePlayerTeam(team.getPlayers(), futures, true);

            // Don't transfer it to the storageManager if there is nothing to do
            if (futures.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Delete permanently a {@link Team} from the storage
     *
     * @param team The {@link Team} to delete
     */
    public CompletableFuture<Void> delete(Team team) {
        Objects.requireNonNull(team);
        lock.lock();
        try {
            // Kick the players from the team
            final PlayerList players = team.getPlayers();
            players.clear();

            // Get the id
            final UUID teamId = team.getId();

            // Create futures
            final List<CompletableFuture<?>> futures = new LinkedList<>();
            // Delete team
            futures.add(
                    storageManager.deleteTeam(teamId).thenRun(() -> teamById.remove(teamId))
            );
            // Save players in the team
            savePlayerTeam(team.getPlayers(), futures, false);

            // Send changes through the network
            networkManager.sendDelete(teamId);

            // Transfer it to the storageManager in all case as there is at least the remove operation
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Save a player team associations of a {@link PlayerList}
     *
     * @param playerList The player list to save
     * @param futures    The future {@link List} to fill with the save action
     * @param network    Whether the changes should be sent through the network
     */
    private void savePlayerTeam(PlayerList playerList, List<CompletableFuture<?>> futures, boolean network) {
        final PlayerTeamTracker tracker = playerList.getPlayerTeamTracker();
        if (tracker.isDirty()) {
            final Map<UUID, PlayerTeamTracker.PlayerChange> changes = tracker.getForSaving();
            if (network) {
                networkManager.sendPlayers(changes);
            }
            futures.add(
                    storageManager.savePlayerTeams(changes).thenRun(() -> linkPlayerTeams(changes))
            );
        }
    }

    /**
     * Link players to their {@link Team}
     *
     * @param map The {@link Map} containing the mapping between players and teams
     */
    private void linkPlayerTeams(Map<UUID, PlayerTeamTracker.PlayerChange> map) {
        for (Map.Entry<UUID, PlayerTeamTracker.PlayerChange> entry : map.entrySet()) {
            final PlayerTeamTracker.PlayerChange playerChange = entry.getValue();
            // Check if it's a remove
            if (playerChange.rank() < 0) {
                playerTeams.remove(entry.getKey());
            } else {
                playerTeams.put(entry.getKey(), playerChange.teamId());
            }
        }
    }

}

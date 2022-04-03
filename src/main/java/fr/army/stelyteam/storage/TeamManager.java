package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.PlayerList;
import fr.army.stelyteam.team.Team;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class TeamManager {

    private final StorageManager storageManager;
    private final ConcurrentMap<UUID, Team> playerTeams;
    private final ConcurrentMap<UUID, Team> teamById;
    private final ReentrantLock lock;

    public TeamManager(StorageManager storageManager) {
        this.storageManager = storageManager;
        playerTeams = new ConcurrentHashMap<>();
        teamById = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
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
     * Get an {@link Optional} {@link UUID} from the cache or the storage
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
     * Load the player {@link Team} from the cache
     *
     * @param playerId The player {@link UUID}
     * @return The {@link Team} of the player.
     * {@code null} if the player does not have a {@link Team} or if he's not loaded
     */
    public Team getPlayerTeam(UUID playerId) {
        Objects.requireNonNull(playerId);
        return playerTeams.get(playerId);
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
        final Team cachedTeam = getPlayerTeam(playerId);
        if (cachedTeam != null) {
            return CompletableFuture.completedFuture(Optional.of(cachedTeam));
        }

        return storageManager.loadPlayerTeam(playerId);
    }

    /**
     * Load the {@link UUID} of the team of a player
     *
     * @param playerId The player {@link UUID}
     * @return The {@link Team} of the player.
     * {@code null} if the player does not have a {@link Team}
     */
    public CompletableFuture<UUID> getPlayerTeamId(UUID playerId) {
        Objects.requireNonNull(playerId);
        final Team cachedTeam = getPlayerTeam(playerId);
        if (cachedTeam != null) {
            return CompletableFuture.completedFuture(cachedTeam.getId());
        }

        return storageManager.getPlayerTeamId(playerId);
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
                futures.add(storageManager.saveTeam(team.getId(), team.getForSaving()));
            }

            // Save players in the team
            final PlayerList owners = team.getOwners();
            final PlayerList members = team.getMembers();
            // Save owners
            if (owners.getPlayerTeamTracker().isDirty()) {
                final Map<UUID, Optional<Team>> changes = owners.getPlayerTeamTracker().getForSaving();
                futures.add(
                        storageManager.savePlayerTeams(changes).thenRun(() -> linkPlayerTeams(changes))
                );
            }
            // Save members
            if (members.getPlayerTeamTracker().isDirty()) {
                final Map<UUID, Optional<Team>> changes = members.getPlayerTeamTracker().getForSaving();
                futures.add(
                        storageManager.savePlayerTeams(changes).thenRun(() -> linkPlayerTeams(changes))
                );
            }

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
            final PlayerList owners = team.getOwners();
            final PlayerList members = team.getMembers();
            owners.clear();
            members.clear();

            // Get the id
            final UUID teamId = team.getId();

            // Create futures
            final List<CompletableFuture<?>> futures = new LinkedList<>();
            // Delete team
            futures.add(
                    storageManager.deleteTeam(teamId).thenRun(() -> teamById.remove(teamId))
            );
            // Save owners
            if (owners.getPlayerTeamTracker().isDirty()) {
                final Map<UUID, Optional<Team>> changes = owners.getPlayerTeamTracker().getForSaving();
                futures.add(
                        storageManager.savePlayerTeams(changes).thenRun(() -> linkPlayerTeams(changes))
                );
            }
            // Save members
            if (members.getPlayerTeamTracker().isDirty()) {
                final Map<UUID, Optional<Team>> changes = members.getPlayerTeamTracker().getForSaving();
                futures.add(
                        storageManager.savePlayerTeams(changes).thenRun(() -> linkPlayerTeams(changes))
                );
            }

            // Transfer it to the storageManager in all case as there is at least the remove operation
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        } finally {
            lock.unlock();
        }
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

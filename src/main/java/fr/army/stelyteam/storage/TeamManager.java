package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;

import java.util.UUID;

public class TeamManager {

    private final Storage storage;

    public TeamManager(Storage storage) {
        this.storage = storage;
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
        return null;
    }

    /**
     * Get a {@link Team} from the cache or the storage
     *
     * @param id The {@link Team}'s id
     * @return The {@link Team} that have the id {@code id}.
     * {@code null} if the {@link Team} does not exist
     */
    public Team getOrLoadTeam(UUID id) {
        return getOrLoadTeam(id, false);
    }

    /**
     * Get a {@link Team} from the cache or the storage
     *
     * @param id           The {@link Team}'s id
     * @param keepInMemory Whether it should keep in memory the {@link Team} if it's loaded from the storage
     * @return The {@link Team} that have the id {@code id}.
     * {@code null} if the {@link Team} does not exist
     */
    public Team getOrLoadTeam(UUID id, boolean keepInMemory) {
        return null;
    }

    /**
     * Load the {@link Team} of a player
     *
     * @param playerId The player {@link UUID}
     */
    public void loadPlayerTeam(UUID playerId) {
    }

    /**
     * Load the player {@link Team} from the cache
     *
     * @param playerId The player {@link UUID}
     * @return The {@link Team} of the player.
     * {@code null} if the player does not have a {@link Team} or if he's not loaded
     */
    public Team getPlayerTeam(UUID playerId) {
        return null;
    }

    /**
     * Load the {@link UUID} of the team of a player
     *
     * @param playerId The player {@link UUID}
     * @return The {@link Team} of the player.
     * {@code null} if the player does not have a {@link Team}
     */
    public UUID getPlayerTeamId(UUID playerId) {
        final Team loadedTeam = getPlayerTeam(playerId);
        if (loadedTeam != null) {
            return loadedTeam.getId();
        }

        // TODO Load it from the storage
        return null;
    }

}

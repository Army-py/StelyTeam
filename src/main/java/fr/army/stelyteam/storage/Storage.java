package fr.army.stelyteam.storage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    void start();

    void stop();

    /**
     * Load a {@link fr.army.stelyteam.team.Team}
     *
     * @param teamID     The {@link UUID} of the Team
     * @param teamFields The {@link TeamField} to load
     * @return A {@link List} of {@link FieldValues} stored in this storage. An empty {@link List} if the
     * {@link fr.army.stelyteam.team.Team} does not exist
     */
    CompletableFuture<List<FieldValues>> loadTeam(UUID teamID, TeamField[] teamFields);

    /**
     * Get the {@link UUID} of the {@link fr.army.stelyteam.team.Team} which has a specific command id
     *
     * @param commandId The command id to research
     * @return An {@link Optional} {@link UUID} which is empty if the {@link fr.army.stelyteam.team.Team} does not exist
     */
    CompletableFuture<Optional<UUID>> getTeamId(String commandId);

    /**
     * Save the changes of a {@link fr.army.stelyteam.team.Team}
     *
     * @param teamId  The {@link fr.army.stelyteam.team.Team}'s {@link UUID}
     * @param changes The changes to save
     * @return A {@link CompletableFuture} that represents the save action
     */
    CompletableFuture<Void> saveTeam(UUID teamId, List<FieldValues> changes);

    /**
     * Delete a {@link fr.army.stelyteam.team.Team} in the storage
     *
     * @param teamId The {@link UUID} of the {@link fr.army.stelyteam.team.Team} to delete
     * @return A {@link CompletableFuture} that represents the delete action
     */
    CompletableFuture<Void> deleteTeam(UUID teamId);

    /**
     * Get the {@link UUID} of the {@link fr.army.stelyteam.team.Team} of a {@link org.bukkit.entity.Player}
     *
     * @param playerId The player {@link UUID}
     * @return An {@link Optional} {@link UUID} which is empty if the {@link fr.army.stelyteam.team.Team} does not exist
     */
    CompletableFuture<Optional<UUID>> getPlayerTeamId(UUID playerId);

    CompletableFuture<Void> savePlayerTeams(Map<UUID, Optional<UUID>> changes);

    record FieldValues(TeamField field, Optional<Object> value) {
    }

}

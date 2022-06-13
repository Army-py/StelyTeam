package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageManager {

    private final StorageFields[] storageFieldsArray;
    private final Storage storagePlayers;
    private final Storage commandIdStorage;
    private final ExecutorService executor;
    private boolean started;

    public StorageManager(StorageDeserializer storageDeserializer) {
        storageFieldsArray = storageDeserializer.getStorageFieldsArray();
        storagePlayers = storageDeserializer.getStoragePlayers();
        commandIdStorage = storageDeserializer.getCommandIdStorage();
        executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "StelyTeam-StorageManager"));
    }

    /**
     * Start the StorageManager
     */
    public void start() {
        if (started) {
            throw new IllegalStateException();
        }
        for (Storage storage : getActivesStorages()) {
            storage.start();
        }
        started = true;
    }

    /**
     * Stop the StorageManager
     */
    public void stop() {
        if (!started) {
            throw new IllegalStateException();
        }
        for (Storage storage : getActivesStorages()) {
            storage.stop();
        }
        executor.shutdown();
        started = false;
    }

    /**
     * Get all active storages
     *
     * @return A {@link Set} of all active storages
     */
    private Set<Storage> getActivesStorages() {
        final Set<Storage> activeStorages = new HashSet<>();
        for (StorageFields storageFields : storageFieldsArray) {
            activeStorages.add(storageFields.storage());
        }
        activeStorages.add(storagePlayers);
        return activeStorages;
    }

    /*
     * Storage methods
     */

    /**
     * Load a {@link Team} from the permanent storage
     *
     * @param teamId The {@link Team}'s {@link UUID}
     * @return An {@link Optional} {@link Team}. The {@link Optional} is empty if the {@link Team} does not exist
     */
    public CompletableFuture<Optional<Team>> loadTeam(UUID teamId) {
        final List<Storage.FieldValues> values = Collections.synchronizedList(new ArrayList<>(TeamField.values().length));
        // Request the field values to the storages
        final CompletableFuture<?>[] storageFutures = new CompletableFuture[storageFieldsArray.length];
        for (int index = 0; index < storageFieldsArray.length; index++) {
            final StorageFields storageFields = storageFieldsArray[index];
            storageFutures[index] = storageFields.storage().loadTeam(
                    teamId,
                    storageFields.fields()
            ).thenAccept(values::addAll); // Store the values
        }

        // Group every future
        final CompletableFuture<Void> allStorageFuture = CompletableFuture.allOf(storageFutures);

        // Create the team
        return allStorageFuture.thenApplyAsync(o -> createTeam(teamId, values), executor);
    }

    /**
     * Load a {@link Team} {@link UUID} from the permanent storage
     *
     * @param commandId The {@link Team}'s command id
     * @return An {@link Optional} {@link Team}'s {@link UUID}
     * The {@link Optional} is empty if the {@link Team} does not exist
     */
    public CompletableFuture<Optional<UUID>> getTeamId(String commandId) {
        return commandIdStorage.getTeamId(commandId);
    }

    /**
     * Create the {@link Team} from the values stored in all {@link Storage}
     *
     * @param teamId {@link Team}'s {@link UUID}
     * @param values A {@link Map} containing every team value
     * @return An {@link Optional} {@link Team} filled by all specified values
     */
    private Optional<Team> createTeam(UUID teamId, List<Storage.FieldValues> values) {
        // If values is empty, it means that the team does not exist
        if (values.isEmpty()) {
            return Optional.empty();
        }

        final Team team = new Team(teamId);

        for (var value : values) {
            if (value.value().isPresent()) {
                value.field().getInflater().inflate(value.value().get(), team);
            }
        }

        return Optional.of(team);
    }

    /**
     * Save a {@link Team} in the permanent storage
     *
     * @param teamId  The {@link UUID} of the {@link Team} to save
     * @param changes The {@link TeamField} to update with their value
     * @return A {@link CompletableFuture} that represents the save action
     */
    public CompletableFuture<Void> saveTeam(UUID teamId, List<Storage.FieldValues> changes) {
        final List<CompletableFuture<?>> saveFutures = new LinkedList<>();

        // For each storage, add save future task if needed
        for (StorageFields storageFields : storageFieldsArray) {
            final List<Storage.FieldValues> values = new LinkedList<>();

            // For each field, add it to the values if it needs to be updated
            for (TeamField field : storageFields.fields()) {
                // Find the matching fieldValue in the changes
                final Optional<Storage.FieldValues> value = changes.stream()
                        .filter(fv -> fv.field() == field)
                        .findAny();
                value.ifPresent(values::add);
            }

            // There is changes to make in this storage
            if (!values.isEmpty()) {
                saveFutures.add(storageFields.storage().saveTeam(teamId, values));
            }
        }

        return CompletableFuture.allOf(saveFutures.toArray(new CompletableFuture[0]));
    }

    /**
     * Delete a {@link Team} from the permanent storage
     *
     * @param teamId The {@link Team}'s {@link UUID}
     * @return A {@link CompletableFuture} that represents the delete action
     */
    public CompletableFuture<Void> deleteTeam(UUID teamId) {
        final CompletableFuture<?>[] deleteFutures = new CompletableFuture[storageFieldsArray.length];
        for (int index = 0; index < storageFieldsArray.length; index++) {
            deleteFutures[index] = storageFieldsArray[index].storage().deleteTeam(teamId);
        }
        return CompletableFuture.allOf(deleteFutures);
    }

    /**
     * Get the player {@link Team}'s {@link UUID}
     *
     * @param playerId The player's {@link UUID}
     * @return An {@link Optional} {@link Team}'s {@link UUID} of the player.
     * An empty {@link Optional} if the player does not have a {@link Team}
     */
    public CompletableFuture<Optional<UUID>> getPlayerTeamId(UUID playerId) {
        return storagePlayers.getPlayerTeamId(playerId);
    }

    /**
     * Save the player team associations
     *
     * @param changes The changes to apply
     * @return A {@link CompletableFuture} that represents the save action
     */
    public CompletableFuture<Void> savePlayerTeams(Map<UUID, PlayerTeamTracker.PlayerChange> changes) {
        return storagePlayers.savePlayerTeams(changes);
    }

    public record StorageFields(Storage storage, TeamField[] fields) {
    }

}

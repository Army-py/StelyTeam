package fr.army.stelyteam.storage;

import com.google.common.collect.Sets;
import fr.army.stelyteam.api.LazyLocation;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamBuilder;
import fr.army.stelyteam.team.TeamField;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class StorageManager {

    private final StorageFields[] storageFieldsArray;
    private final StorageFields[] storagePlayersArray;
    private final Storage commandIdStorage;
    private final ExecutorService executor;
    private boolean started;

    public StorageManager(YamlConfiguration config, Logger logger) {
        final Map<String, Storage> storageByName = deserializeStorages(config, logger);
        final Map<TeamField, Storage> fieldStorage = deserializeFieldAssociations(
                config,
                storageByName,
                "fields",
                TeamField.values()
        );
        commandIdStorage = fieldStorage.get(TeamField.COMMAND_ID);
        storageFieldsArray = createStorageFieldsArray(fieldStorage);
        final Map<TeamField, Storage> playerStorage = deserializeFieldAssociations(
                config,
                storageByName,
                "players",
                TeamField.OWNERS, TeamField.MEMBERS
        );
        storagePlayersArray = createStorageFieldsArray(playerStorage);
        executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "StelyTeam-StorageManager"));
    }

    /**
     * Create the storages from the storage.yml configuration
     *
     * @param config The storage.yml {@link YamlConfiguration}
     * @param logger The plugin's {@link Logger} to log information if there is something wrong
     * @return A {@link Map} of the specified storages by name
     */
    private Map<String, Storage> deserializeStorages(YamlConfiguration config, Logger logger) {
        final Map<String, Storage> storageByName = new HashMap<>();
        final ConfigurationSection storagesSection = config.getConfigurationSection("storages");
        if (storagesSection == null) {
            throw new RuntimeException("There is no storage section in the storage.yml");
        }

        // Deserialize storages
        for (String storageKey : storagesSection.getKeys(false)) {
            final ConfigurationSection storageSection = storagesSection.getConfigurationSection(storageKey);
            if (storageSection == null) {
                logger.warning("There is a key in the storage.yml that does not represent a section, skip it");
                continue;
            }

            final String storageType = storageSection.getString("type");
            if (storageType == null) {
                logger.warning("The storage section '" + storageKey + "' in the storage.yml does not define a storage type.");
                continue;
            }
            final Storage storage;
            switch (storageType) {
                case "MySQL" -> storage = deserializeMySQL(storageSection);
                case "SQLite" -> storage = deserializeSQLite(storageSection);
                case "Yaml" -> storage = deserializeYaml(storageSection);
                default -> {
                    logger.warning("The storage section '" + storageKey + "' in the storage.yml define an unknown type." +
                            "Supported types are MySQL, SQLite and Yaml");
                    continue;
                }
            }
            if (storage == null) {
                continue;
            }
            storageByName.put(storageKey, storage);
        }

        return storageByName;
    }

    /*
     * Deserialize methods
     */

    private Storage deserializeMySQL(ConfigurationSection storageSection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Storage deserializeSQLite(ConfigurationSection storageSection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Storage deserializeYaml(ConfigurationSection storageSection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Deserialize field associations from the configuration
     *
     * @param config        The {@link YamlConfiguration} to deserialize
     * @param storageByName The {@link Map} that contains all usable storages
     * @param sectionName   The section to deserialize
     * @return A {@link Map} of all storage fields associations
     */
    private Map<TeamField, Storage> deserializeFieldAssociations(
            YamlConfiguration config, Map<String, Storage> storageByName, String sectionName, TeamField... fieldsValues
    ) {
        final Map<TeamField, Storage> fieldStorage = new HashMap<>();
        final ConfigurationSection fieldSection = config.getConfigurationSection(sectionName);
        if (fieldSection == null) {
            throw new RuntimeException("The storage.yml does not contains the '" + sectionName + "' section");
        }
        for (TeamField teamField : fieldsValues) {
            final String storageName = fieldSection.getString(teamField.name());
            if (storageName == null) {
                throw new RuntimeException("The field '" + teamField + "' does not specify a storage in the '"
                        + sectionName + "' section of the storage.yml");
            }
            final Storage storage = storageByName.get(storageName);
            if (storage == null) {
                throw new RuntimeException("'" + storageName + "' is not a registered storage in storage.yml");
            }

            fieldStorage.put(teamField, storage);
        }

        return fieldStorage;
    }


    /**
     * Create the {@link StorageFields} array
     */
    private StorageFields[] createStorageFieldsArray(Map<TeamField, Storage> fieldStorage) {
        // Map every storage to a set of fields
        final Map<Storage, Set<TeamField>> storageFields = new HashMap<>();
        for (var entry : fieldStorage.entrySet()) {
            final Set<TeamField> fields = storageFields.computeIfAbsent(entry.getValue(), k -> new HashSet<>());
            fields.add(entry.getKey());
        }

        final StorageFields[] fields = new StorageFields[storageFields.size()];
        int index = 0;
        for (var entry : storageFields.entrySet()) {
            fields[index++] = new StorageFields(entry.getKey(), entry.getValue().toArray(new TeamField[0]));
        }
        return fields;
    }

    /*
     * Public methods
     */

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
        for (StorageFields storageFields : storagePlayersArray) {
            activeStorages.add(storageFields.storage());
        }
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
        final Map<TeamField, Optional<Object>> values = Collections.synchronizedMap(new EnumMap<>(TeamField.class));
        // Send the fields to the storages
        final CompletableFuture<?>[] storageFutures = new CompletableFuture[storageFieldsArray.length];
        for (int index = 0; index < storageFieldsArray.length; index++) {
            final StorageFields storageFields = storageFieldsArray[index];
            storageFutures[index] = storageFields.storage().loadTeam(
                    teamId,
                    storageFields.fields()
            ).thenAccept(values::putAll); // Store the values
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
    private Optional<Team> createTeam(UUID teamId, Map<TeamField, Optional<Object>> values) {
        if (values.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new TeamBuilder()
                .setId(teamId)
                .setCommandId((String) values.get(TeamField.COMMAND_ID).orElseThrow(RuntimeException::new))
                .setPrefix((String) values.get(TeamField.PREFIX).orElse(null))
                .setSuffix((String) values.get(TeamField.SUFFIX).orElse(null))
                .setCreator((UUID) values.get(TeamField.CREATOR).orElse(null))
                .setCreationDate((Date) values.get(TeamField.CREATION_DATE).orElse(null))
                .setLevel((int) values.get(TeamField.LEVEL).orElse(0))
                .setBankAccount((boolean) values.get(TeamField.BANK_ACCOUNT).orElse(false))
                .setMoney((double) values.get(TeamField.MONEY).orElse(0d))
                .setHome((LazyLocation) values.get(TeamField.HOME).orElse(null))
                .setOwners(Sets.newHashSet((UUID[]) values.get(TeamField.OWNERS).orElse(new UUID[0])))
                .setMembers(Sets.newHashSet((UUID[]) values.get(TeamField.MEMBERS).orElse(new UUID[0])))
                .create()
        );
    }

    /**
     * Save a {@link Team} in the permanent storage
     *
     * @param teamId  The {@link UUID} of the {@link Team} to save
     * @param changes The {@link TeamField} to update with their value
     * @return A {@link CompletableFuture} that represents the save action
     */
    public CompletableFuture<Void> saveTeam(UUID teamId, Map<TeamField, Optional<Object>> changes) {
        final List<CompletableFuture<?>> saveFutures = new LinkedList<>();

        // For each storage, add save future task if needed
        for (StorageFields storageFields : storageFieldsArray) {
            final List<Storage.FieldValues> values = new LinkedList<>();

            // For each field, add it to the values if it needs to be updated
            for (TeamField field : storageFields.fields()) {
                final Optional<Object> value = changes.get(field);
                // If the value is null, then it does not need to be updated
                if (value != null) {
                    values.add(new Storage.FieldValues(field, value));
                }
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
        final List<UUID> teamIds = Collections.synchronizedList(new LinkedList<>());
        // Register all associations from all storages
        final CompletableFuture<?>[] loadFutures = new CompletableFuture[storagePlayersArray.length];
        for (int index = 0; index < storagePlayersArray.length; index++) {
            loadFutures[index] = storagePlayersArray[index].storage()
                    .getPlayerTeamId(playerId)
                    .thenAcceptAsync(teamId -> teamId.ifPresent(teamIds::add), executor);
        }

        // Group all futures and create a last one that get the first player association
        return CompletableFuture.allOf(loadFutures)
                .thenCompose(o -> CompletableFuture.completedFuture(
                        teamIds.isEmpty() ? Optional.empty() : Optional.of(teamIds.get(0)))
                );
    }

    /**
     * Save the player team associations
     *
     * @param teamField The {@link TeamField} to save
     * @param changes   The changes to apply
     * @return A {@link CompletableFuture} that represents the save action
     */
    public CompletableFuture<Void> savePlayerTeams(TeamField teamField, Map<UUID, Optional<UUID>> changes) {
        // Retrieve the Storage
        // (could use a map but there is currently maximum 2 elements and the array is faster for the load method)
        for (StorageFields storageFields : storagePlayersArray) {
            for (TeamField field : storageFields.fields()) {
                if (field == teamField) {
                    // We found the good storage, load the player from it
                    return storageFields.storage().savePlayerTeams(changes);
                }
            }
        }
        // Impossible
        throw new RuntimeException("Could not find the storage for the " + teamField + " player association.");
    }

    private record StorageFields(Storage storage, TeamField[] fields) {
    }

}

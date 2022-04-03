package fr.army.stelyteam.storage;

import fr.army.stelyteam.api.LazyLocation;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamField;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class StorageManager {

    private final StorageFields[] storageFieldsArray;
    private final Executor executor;
    private final Storage commandIdStorage;

    public StorageManager(YamlConfiguration config, Logger logger) {

        //TODO Init StorageFieldsArray

        final Map<TeamField, Storage> fieldStorage;
        final Map<Storage, Set<TeamField>> storageField;


        fieldStorage = Collections.synchronizedMap(new EnumMap<>(TeamField.class));
        fieldStorage.putAll(initStorages(config, logger));
        storageField = new ConcurrentHashMap<>();
        loadStorageByField();

        commandIdStorage = fieldStorage.get(TeamField.COMMAND_ID);

        executor = null; // TODO Init this executor
    }

    /**
     * Load the storageField map from the fieldStorage map
     */
    private void loadStorageByField() {
        for (Map.Entry<TeamField, Storage> entry : fieldStorage.entrySet()) {
            final Set<TeamField> fields = storageField.computeIfAbsent(entry.getValue(), k -> new HashSet<>());
            fields.add(entry.getKey());
        }
    }

    /**
     * Create the storages from the storage.yml configuration
     *
     * @param config The storage.yml {@link YamlConfiguration}
     * @param logger The plugin's {@link Logger} to log information if there is something wrong
     * @return A {@link Set} of the used storages
     */
    private Map<TeamField, Storage> initStorages(YamlConfiguration config, Logger logger) {
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
                case "MySQL" -> storage = initMySQL(storageSection);
                case "SQLite" -> storage = initSQLite(storageSection);
                case "Yaml" -> storage = initYaml(storageSection);
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

        // Deserialize storage associations
        final Map<TeamField, Storage> fieldStorage = new EnumMap<>(TeamField.class);
        final ConfigurationSection fieldSection = config.getConfigurationSection("fields");
        if (fieldSection == null) {
            throw new RuntimeException("The storage.yml does not contains the field section");
        }
        for (TeamField teamField : TeamField.values()) {
            final String storageName = fieldSection.getString(teamField.name());
            if (storageName == null) {
                throw new RuntimeException("The field '" + teamField + "' does not specify a storage in storage.yml");
            }
            final Storage storage = storageByName.get(storageName);
            if (storage == null) {
                throw new RuntimeException("'" + storageName + "' is not a registered storage in storage.yml");
            }

            fieldStorage.put(teamField, storage);
        }

        return fieldStorage;
    }

    /*
     * Init methods
     */

    private Storage initMySQL(ConfigurationSection storageSection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Storage initSQLite(ConfigurationSection storageSection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Storage initYaml(ConfigurationSection storageSection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


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
        return Optional.of(new Team(
                        teamId,
                        (String) values.get(TeamField.COMMAND_ID).orElseThrow(RuntimeException::new),
                        (String) values.get(TeamField.PREFIX).orElse(null),
                        (String) values.get(TeamField.SUFFIX).orElse(null),
                        (UUID) values.get(TeamField.CREATOR).orElse(null),
                        (Date) values.get(TeamField.CREATION_DATE).orElse(null),
                        (int) values.get(TeamField.LEVEL).orElse(0),
                        (boolean) values.get(TeamField.BANK_ACCOUNT).orElse(false),
                        (double) values.get(TeamField.MONEY).orElse(0d),
                        (LazyLocation) values.get(TeamField.HOME).orElse(null),
                        new HashSet<>(Arrays.asList((UUID[]) values.get(TeamField.OWNERS).orElse(new UUID[0]))),
                        new HashSet<>(Arrays.asList((UUID[]) values.get(TeamField.MEMBERS).orElse(new UUID[0])))
                )
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

    public CompletableFuture<UUID> getPlayerTeamId(UUID playerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public CompletableFuture<Void> savePlayerTeams(Map<UUID, Optional<Team>> changes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public CompletableFuture<Optional<Team>> loadPlayerTeam(UUID playerId) {
        // .getPlayerTeamId(playerId).thenCompose(storageManager::loadTeam)
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private record StorageFields(Storage storage, TeamField[] fields) {
    }

}

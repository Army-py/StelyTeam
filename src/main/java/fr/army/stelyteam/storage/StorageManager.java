package fr.army.stelyteam.storage;

import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamField;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class StorageManager {

    private final Map<TeamField, Storage> fieldStorage;

    public StorageManager(YamlConfiguration config, Logger logger) {
        fieldStorage = Collections.synchronizedMap(new EnumMap<>(TeamField.class));
        fieldStorage.putAll(initStorages(config, logger));
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

    private Storage initMySQL(ConfigurationSection storageSection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Storage initSQLite(ConfigurationSection storageSection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Storage initYaml(ConfigurationSection storageSection) {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    public CompletableFuture<Team> loadTeam(UUID teamId) {
        final Map<TeamField, Optional<Object>> values = Collections.synchronizedMap(new EnumMap<>(TeamField.class));
        // Prepare which field will be sent to which storage
        final Map<Storage, Set<TeamField>> storageByField = new HashMap<>();
        for (Map.Entry<TeamField, Storage> entry : fieldStorage.entrySet()) {
            final Set<TeamField> fields = storageByField.computeIfAbsent(entry.getValue(), k -> new HashSet<>());
            fields.add(entry.getKey());
        }
        // Send the fields to the storages
        final CompletableFuture<?>[] storageFutures = new CompletableFuture[storageByField.size()];
        int index = 0;
        for (Map.Entry<Storage, Set<TeamField>> entry : storageByField.entrySet()) {
            storageFutures[index] = entry.getKey().loadTeam(
                    teamId,
                    entry.getValue()
            ).thenAccept(values::putAll); // Store the values
            index++;
        }

        // Group every future
        final CompletableFuture<Void> allStorageFuture = CompletableFuture.allOf(storageFutures);

        // Create the team
        return allStorageFuture.thenApply(o -> createTeam(teamId, values));
    }

    /**
     * Create the {@link Team} from the values stored in all {@link Storage}
     *
     * @param teamId {@link Team}'s {@link UUID}
     * @param values A {@link Map} containing every team value
     * @return A {@link Team} filled by all specified values
     */
    private Team createTeam(UUID teamId, Map<TeamField, Optional<Object>> values) {
        return null;
    }

    public CompletableFuture<Void> saveTeam(UUID teamId, Map<TeamField, Optional<Object>> changes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public CompletableFuture<Void> deleteTeam(UUID teamId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public CompletableFuture<UUID> getPlayerTeamId(UUID playerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public CompletableFuture<Void> savePlayerTeams(Map<UUID, Optional<Team>> changes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}

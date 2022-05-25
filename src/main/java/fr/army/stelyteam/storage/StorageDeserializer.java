package fr.army.stelyteam.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class StorageDeserializer {

    private final YamlConfiguration config;
    private final Logger logger;
    private StorageManager.StorageFields[] storageFieldsArray;
    private Storage storagePlayers;
    private Storage commandIdStorage;

    public StorageDeserializer(YamlConfiguration config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    public void load() {
        final Map<String, Storage> storageByName = deserializeStorages(config, logger);
        final Map<TeamField, Storage> fieldStorage = deserializeFieldAssociations(storageByName);

        commandIdStorage = fieldStorage.get(TeamField.COMMAND_ID);
        storageFieldsArray = createStorageFieldsArray(fieldStorage);
        storagePlayers = getPlayerStorage(storageByName);
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

    // TODO Implement deserialize methods

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
     * @param storageByName The {@link Map} that contains all usable storages
     * @return A {@link Map} of all storage fields associations
     */
    private Map<TeamField, Storage> deserializeFieldAssociations(Map<String, Storage> storageByName) {
        final Map<TeamField, Storage> fieldStorage = new HashMap<>();
        final ConfigurationSection fieldSection = config.getConfigurationSection("fields");
        if (fieldSection == null) {
            throw new RuntimeException("The storage.yml does not contains the 'fields' section");
        }
        for (TeamField teamField : TeamField.values()) {
            final String storageName = fieldSection.getString(teamField.name());
            if (storageName == null) {
                throw new RuntimeException("The field '" + teamField + "' does not specify a storage in the" +
                        " 'fields' section of the storage.yml");
            }

            final Storage storage = getStorage(storageByName, storageName);

            fieldStorage.put(teamField, storage);
        }

        return fieldStorage;
    }

    /**
     * Deserialize players storage association
     *
     * @param storageByName The {@link Map} that contains all usable storages
     * @return The player {@link Storage}
     */
    private Storage getPlayerStorage(Map<String, Storage> storageByName) {
        final String storageName = config.getString("players");
        if (storageName == null) {
            throw new RuntimeException("The storage.yml does not specify a 'players' storage");
        }

        return getStorage(storageByName, storageName);
    }

    /**
     * Create the {@link StorageManager.StorageFields} array
     */
    private StorageManager.StorageFields[] createStorageFieldsArray(Map<TeamField, Storage> fieldStorage) {
        // Map every storage to a set of fields
        final Map<Storage, Set<TeamField>> storageFields = new HashMap<>();
        for (var entry : fieldStorage.entrySet()) {
            final Set<TeamField> fields = storageFields.computeIfAbsent(entry.getValue(), k -> new HashSet<>());
            fields.add(entry.getKey());
        }

        final StorageManager.StorageFields[] fields = new StorageManager.StorageFields[storageFields.size()];
        int index = 0;
        for (var entry : storageFields.entrySet()) {
            fields[index++] = new StorageManager.StorageFields(entry.getKey(), entry.getValue().toArray(new TeamField[0]));
        }
        return fields;
    }

    /**
     * Get a storage from the loaded storage
     *
     * @param storageByName The {@link Map} that contains all usable storages
     * @param storageName   The name of the {@link Storage}
     * @return The loaded {@link Storage} with the name {@code storageName}
     */
    private Storage getStorage(Map<String, Storage> storageByName, String storageName) {
        final Storage storage = storageByName.get(storageName);
        if (storage == null) {
            throw new RuntimeException("'" + storageName + "' is not a registered storage in storage.yml");
        }
        return storage;
    }

    public StorageManager.StorageFields[] getStorageFieldsArray() {
        return storageFieldsArray;
    }

    public Storage getStoragePlayers() {
        return storagePlayers;
    }

    public Storage getCommandIdStorage() {
        return commandIdStorage;
    }
}

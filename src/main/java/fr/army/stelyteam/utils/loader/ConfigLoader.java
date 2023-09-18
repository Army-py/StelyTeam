package fr.army.stelyteam.utils.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.utils.loader.exception.UnableLoadConfigException;

public class ConfigLoader {
    
    private final Plugin plugin;


    public ConfigLoader(Plugin plugin) {
        this.plugin = plugin;
    }

    public YamlConfiguration initFile(@NotNull String fileName) throws UnableLoadConfigException {
        final File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(plugin.getResource(fileName)), file.toPath());
            } catch (IOException ignored) {
                throw new UnableLoadConfigException(fileName, "Unable to copy file from resources");
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}

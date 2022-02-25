package fr.army;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import fr.army.commands.CommandStelyTeam;
import fr.army.events.InventoryClick;
import fr.army.utils.SQLManager;

public class App extends JavaPlugin {
    public static App instance;
    public static YamlConfiguration config;
    public static SQLManager sqlManager;

    public static ArrayList<String> playersCreateTeam = new ArrayList<String>();


    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        config = initFile(this.getDataFolder(), "config.yml");

        App.sqlManager = new SQLManager();
        try {
            sqlManager.connect();
            this.getLogger().info("SQL connectée au plugin !");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
        
        getCommand("stelyteam").setExecutor(new CommandStelyTeam());
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);

        getLogger().info("StelyTeam ON");
    }


    @Override
    public void onDisable() {
        // sqlite.close();
        getLogger().info("StelyTeam OFF");
    }


    private YamlConfiguration initFile(File dataFolder, String fileName) {
        final File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getResource(fileName)), file.toPath());
            } catch (IOException ignored) {
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}

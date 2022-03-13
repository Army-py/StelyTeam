package fr.army.stelyteam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import fr.army.stelyteam.commands.CmdStelyTeam;
import fr.army.stelyteam.events.InventoryClickManager;
import fr.army.stelyteam.utils.SQLManager;
import fr.army.stelyteam.utils.SQLiteManager;

public class App extends JavaPlugin {
    public static App instance;
    public static YamlConfiguration config;
    public static SQLManager sqlManager;
    public static SQLiteManager sqliteManager;

    public static ArrayList<String> playersCreateTeam = new ArrayList<String>();

    public static ArrayList<String> playersJoinTeam = new ArrayList<String>();
    public static ArrayList<String> teamsJoinTeam = new ArrayList<String>();
    public static ArrayList<String> playersKickTeam = new ArrayList<String>();
    public static ArrayList<String> teamsKickTeam = new ArrayList<String>();
    public static ArrayList<String> playersBuyTeamBank = new ArrayList<String>();


    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        config = initFile(this.getDataFolder(), "config.yml");

        App.sqlManager = new SQLManager();
        App.sqliteManager = new SQLiteManager();
        try {
            sqlManager.connect();
            sqliteManager.connect();
            this.getLogger().info("SQL connectée au plugin !");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
        sqliteManager.createTables();
        
        getCommand("stelyteam").setExecutor(new CmdStelyTeam());
        getCommand("stelyteam").setTabCompleter(new CmdStelyTeam());
        getServer().getPluginManager().registerEvents(new InventoryClickManager(), this);

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

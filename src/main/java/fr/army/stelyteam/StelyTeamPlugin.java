package fr.army.stelyteam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import fr.army.stelyteam.commands.CmdStelyTeam;
import fr.army.stelyteam.events.InventoryClickManager;
import fr.army.stelyteam.events.InventoryClose;
import fr.army.stelyteam.events.PlayerQuit;
import fr.army.stelyteam.utils.SQLManager;
import fr.army.stelyteam.utils.SQLiteManager;

public class StelyTeamPlugin extends JavaPlugin {
    public static StelyTeamPlugin instance;
    public static YamlConfiguration config;
    public static SQLManager sqlManager;
    public static SQLiteManager sqliteManager;
    public static Economy economy = null;

    public static ArrayList<String> playersCreateTeam = new ArrayList<String>();

    // (playerName, actionName)
    public static HashMap<String, String> playersTempActions = new HashMap<String, String>();
    // {sender, receiver, teamId, actionName}
    public static ArrayList<String[]> teamsTempActions = new ArrayList<String[]>();
    // {owner, name, prefix}
    public static ArrayList<String[]> createTeamTemp = new ArrayList<String[]>();


    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        config = initFile(this.getDataFolder(), "config.yml");

        StelyTeamPlugin.sqlManager = new SQLManager();
        StelyTeamPlugin.sqliteManager = new SQLiteManager();
        try {
            sqlManager.connect();
            sqliteManager.connect();
            this.getLogger().info("SQL connectée au plugin !");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
        
        sqlManager.createTables();
        sqliteManager.createTables();
        
        getCommand("stelyteam").setExecutor(new CmdStelyTeam());
        getCommand("stelyteam").setTabCompleter(new CmdStelyTeam());
        getServer().getPluginManager().registerEvents(new InventoryClickManager(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new InventoryClose(), this);

        getLogger().info("StelyTeam ON");
    }


    @Override
    public void onDisable() {
        // sqlite.close();
        sqlManager.disconnect();
        sqliteManager.disconnect();
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


    public static boolean setupEconomy(){
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null); 
	}


    public static String[] getTeamActions(String playerName) {
        for (String[] strings : teamsTempActions) {
            if (strings[0].equals(playerName) || strings[1].equals(playerName)) {
                return strings;
            }
        }
        return null;
    }

    public static void addTeamTempAction(String sender, String receiver, String teamId, String action) {
        teamsTempActions.add(new String[]{sender, receiver, teamId, action});
    }

    public static void removeTeamTempAction(String playerName) {
        for (String[] strings : teamsTempActions) {
            if (strings[0].equals(playerName) || strings[1].equals(playerName)) {
                teamsTempActions.remove(strings);
                return;
            }
        }
    }

    public static boolean containTeamAction(String playerName, String actionName) {
        if (teamsTempActions.isEmpty()) return false;
        for (String[] strings : teamsTempActions) {
            if ((strings[0].equals(playerName) || strings[1].equals(playerName)) && strings[3].equals(actionName)) {
                return true;
            }
        }
        return false;
    }


    public static String[] getCreationTeamTemp(String playerName) {
        for (String[] strings : createTeamTemp) {
            if (strings[0].equals(playerName)) {
                return strings;
            }
        }
        return null;
    }

    public static void addCreationTeamTempName(String owner, String teamId) {
        createTeamTemp.add(new String[]{owner, teamId, ""});
    }

    public static void addCreationTeamTempPrefix(String playerName, String prefix) {
        for (int i = 0; i < createTeamTemp.size(); i++) {
            if (createTeamTemp.get(i)[0].equals(playerName)) {
                createTeamTemp.get(i)[2] = prefix;
                return;
            }
        }
    }

    public static void removeCreationTeamTemp(String playerName) {
        for (String[] strings : createTeamTemp) {
            if (strings[0].equals(playerName)) {
                createTeamTemp.remove(strings);
                return;
            }
        }
    }

    public static boolean containCreationTeamTemp(String playerName) {
        if (createTeamTemp.isEmpty()) return false;
        for (String[] strings : createTeamTemp) {
            if (strings[0].equals(playerName)) {
                return true;
            }
        }
        return false;
    }


    public static String getPlayerActions(String playerName) {
        return playersTempActions.get(playerName);
    }


    public static void removePlayerTempAction(String playerName) {
        playersTempActions.remove(playerName);
    }


    public static boolean containPlayerTempAction(String playerName, String actionName) {
        if (playersTempActions.isEmpty()) return false;
        if (playersTempActions.containsKey(playerName) && playersTempActions.get(playerName).equals(actionName)) {
            return true;
        }else{
            return false;
        }
    }


    public static String getRankFromId(Integer value) {
        for (String key : StelyTeamPlugin.config.getConfigurationSection("ranks").getKeys(false)) {
            if (StelyTeamPlugin.config.getInt("ranks." + key + ".id") == value) {
                return key;
            }
        }
        return null;
    }


    public static Integer getLastRank(){
        Integer lastRank = 0;
        for (String key : StelyTeamPlugin.config.getConfigurationSection("ranks").getKeys(false)) {
            if (StelyTeamPlugin.config.getInt("ranks." + key + ".id") > lastRank) {
                lastRank = StelyTeamPlugin.config.getInt("ranks." + key + ".id");
            }
        }
        return lastRank;
    }
}

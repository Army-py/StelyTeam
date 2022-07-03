package fr.army.stelyteam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import fr.army.stelyteam.commands.CommandManager;
import fr.army.stelyteam.events.InventoryClickManager;
import fr.army.stelyteam.events.InventoryClose;
import fr.army.stelyteam.events.PlayerQuit;
import fr.army.stelyteam.utils.ColorsBuilder;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;
import fr.army.stelyteam.utils.SQLiteManager;

public class StelyTeamPlugin extends JavaPlugin {
    private YamlConfiguration config;
    private YamlConfiguration messages;
    private SQLManager sqlManager;
    private SQLiteManager sqliteManager;
    private EconomyManager economy;
    private CommandManager commandManager;
    private MessageManager messageManager;
    private ColorsBuilder colorsBuilder;

    public ArrayList<String> playersCreateTeam = new ArrayList<String>();

    // (playerName, actionName)
    public HashMap<String, String> playersTempActions = new HashMap<String, String>();
    // {sender, receiver, teamId, actionName}
    public ArrayList<String[]> teamsTempActions = new ArrayList<String[]>();
    // {owner, name, prefix}
    public ArrayList<String[]> createTeamTemp = new ArrayList<String[]>();


    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.config = initFile(this.getDataFolder(), "config.yml");
        this.messages = initFile(this.getDataFolder(), "messages.yml");

        this.sqlManager = new SQLManager();
        this.sqliteManager = new SQLiteManager();

        try {
            this.sqlManager.connect();
            this.sqliteManager.connect();
            this.getLogger().info("SQL connectée au plugin !");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
        
        this.sqlManager.createTables();
        this.sqliteManager.createTables();
        this.economy = new EconomyManager(this);
        this.messageManager = new MessageManager(this);
        this.commandManager = new CommandManager(this);
        this.colorsBuilder = new ColorsBuilder();
        
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


    public String[] getTeamActions(String playerName) {
        for (String[] strings : teamsTempActions) {
            if (strings[0].equals(playerName) || strings[1].equals(playerName)) {
                return strings;
            }
        }
        return null;
    }

    public void addTeamTempAction(String sender, String receiver, String teamId, String action) {
        teamsTempActions.add(new String[]{sender, receiver, teamId, action});
    }

    public void removeTeamTempAction(String playerName) {
        for (String[] strings : teamsTempActions) {
            if (strings[0].equals(playerName) || strings[1].equals(playerName)) {
                teamsTempActions.remove(strings);
                return;
            }
        }
    }

    public boolean containTeamAction(String playerName, String actionName) {
        if (teamsTempActions.isEmpty()) return false;
        for (String[] strings : teamsTempActions) {
            if ((strings[0].equals(playerName) || strings[1].equals(playerName)) && strings[3].equals(actionName)) {
                return true;
            }
        }
        return false;
    }


    public String[] getCreationTeamTemp(String playerName) {
        for (String[] strings : createTeamTemp) {
            if (strings[0].equals(playerName)) {
                return strings;
            }
        }
        return null;
    }

    public void addCreationTeamTempName(String owner, String teamId) {
        createTeamTemp.add(new String[]{owner, teamId, ""});
    }

    public void addCreationTeamTempPrefix(String playerName, String prefix) {
        for (int i = 0; i < createTeamTemp.size(); i++) {
            if (createTeamTemp.get(i)[0].equals(playerName)) {
                createTeamTemp.get(i)[2] = prefix;
                return;
            }
        }
    }

    public void removeCreationTeamTemp(String playerName) {
        for (String[] strings : createTeamTemp) {
            if (strings[0].equals(playerName)) {
                createTeamTemp.remove(strings);
                return;
            }
        }
    }

    public boolean containCreationTeamTemp(String playerName) {
        if (createTeamTemp.isEmpty()) return false;
        for (String[] strings : createTeamTemp) {
            if (strings[0].equals(playerName)) {
                return true;
            }
        }
        return false;
    }


    public String getPlayerActions(String playerName) {
        return playersTempActions.get(playerName);
    }


    public void removePlayerTempAction(String playerName) {
        playersTempActions.remove(playerName);
    }


    public boolean containPlayerTempAction(String playerName, String actionName) {
        if (playersTempActions.isEmpty()) return false;
        if (playersTempActions.containsKey(playerName) && playersTempActions.get(playerName).equals(actionName)) {
            return true;
        }else{
            return false;
        }
    }


    public String getRankFromId(Integer value) {
        for (String key : config.getConfigurationSection("ranks").getKeys(false)) {
            if (config.getInt("ranks." + key + ".id") == value) {
                return key;
            }
        }
        return null;
    }


    public Integer getLastRank(){
        Integer lastRank = 0;
        for (String key : config.getConfigurationSection("ranks").getKeys(false)) {
            if (config.getInt("ranks." + key + ".id") > lastRank) {
                lastRank = config.getInt("ranks." + key + ".id");
            }
        }
        return lastRank;
    }


    public YamlConfiguration getConfig() {
        return config;
    }

    public YamlConfiguration getMessages() {
        return messages;
    }

    public SQLManager getSQLManager() {
        return sqlManager;
    }

    public SQLiteManager getSQLiteManager() {
        return sqliteManager;
    }

    public EconomyManager getEconomy() {
        return economy;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ColorsBuilder getColorsBuilder() {
        return colorsBuilder;
    }
}

package fr.army.stelyteam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import fr.army.stelyteam.commands.CommandManager;
import fr.army.stelyteam.events.InventoryClickManager;
import fr.army.stelyteam.events.InventoryCloseManager;
import fr.army.stelyteam.events.PlayerQuit;
import fr.army.stelyteam.utils.TeamMembersUtils;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;
import fr.army.stelyteam.utils.manager.SQLiteManager;
import fr.army.stelyteam.utils.manager.SerializeManager;

public class StelyTeamPlugin extends JavaPlugin {

    private YamlConfiguration config;
    private YamlConfiguration messages;
    private CacheManager cacheManager;
    private SQLManager sqlManager;
    private SQLiteManager sqliteManager;
    private EconomyManager economyManager;
    private CommandManager commandManager;
    private MessageManager messageManager;
    private ColorsBuilder colorsBuilder;
    private ConversationBuilder conversationBuilder;
    private InventoryBuilder inventoryBuilder;
    private TeamMembersUtils teamMembersUtils;
    private SerializeManager serializeManager;


    public ArrayList<String> playersCreateTeam = new ArrayList<String>();

    // (playerName, actionName)
    public HashMap<String, String> playersTempActions = new HashMap<String, String>();
    // {sender, receiver, teamId, actionName}
    public ArrayList<String[]> teamsTempActions = new ArrayList<String[]>();
    // {owner, name, prefix}
    public ArrayList<String[]> createTeamTemp = new ArrayList<String[]>();
    // {teamId, storageInstance, storageId, content}
    public ArrayList<Object[]> storageTemp = new ArrayList<Object[]>();


    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.config = initFile(this.getDataFolder(), "config.yml");
        this.messages = initFile(this.getDataFolder(), "messages.yml");

        this.sqlManager = new SQLManager(this);
        this.sqliteManager = new SQLiteManager(this);

        try {
            this.sqlManager.connect();
            this.sqliteManager.connect();
            this.getLogger().info("SQL connectée au plugin !");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
        
        this.cacheManager = new CacheManager();
        this.sqlManager.createTables();
        this.sqliteManager.createTables();
        this.economyManager = new EconomyManager(this);
        this.messageManager = new MessageManager(this);
        this.commandManager = new CommandManager(this);
        this.colorsBuilder = new ColorsBuilder();
        this.conversationBuilder = new ConversationBuilder(this);
        this.inventoryBuilder = new InventoryBuilder(this);
        this.teamMembersUtils = new TeamMembersUtils(this);
        this.serializeManager = new SerializeManager();
        
        getServer().getPluginManager().registerEvents(new InventoryClickManager(this), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseManager(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);

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


    // getTempAction
    public String[] getTeamActions(String playerName) {
        for (String[] strings : teamsTempActions) {
            if (strings[0].equals(playerName) || strings[1].equals(playerName)) {
                return strings;
            }
        }
        return null;
    }

    // addTempAction
    public void addTeamTempAction(String sender, String receiver, String teamId, String action) {
        teamsTempActions.add(new String[]{sender, receiver, teamId, action});
    }

    // removePlayerAction
    public void removeTeamTempAction(String playerName) {
        for (String[] strings : teamsTempActions) {
            if (strings[0].equals(playerName) || strings[1].equals(playerName)) {
                teamsTempActions.remove(strings);
                return;
            }
        }
    }

    // playerHasActionName
    public boolean containTeamAction(String playerName, String actionName) {
        if (teamsTempActions.isEmpty()) return false;
        for (String[] strings : teamsTempActions) {
            if ((strings[0].equals(playerName) || strings[1].equals(playerName)) && strings[3].equals(actionName)) {
                return true;
            }
        }
        return false;
    }

    // getTempAction
    public String[] getCreationTeamTemp(String playerName) {
        for (String[] strings : createTeamTemp) {
            if (strings[0].equals(playerName)) {
                return strings;
            }
        }
        return null;
    }

    // addTempAction
    public void addCreationTeamTempName(String owner, String teamId) {
        createTeamTemp.add(new String[]{owner, teamId, ""});
    }

    // setActionTeamPrefix
    public void addCreationTeamTempPrefix(String playerName, String prefix) {
        for (int i = 0; i < createTeamTemp.size(); i++) {
            if (createTeamTemp.get(i)[0].equals(playerName)) {
                createTeamTemp.get(i)[2] = prefix;
                return;
            }
        }
    }

    // removePlayerAction
    public void removeCreationTeamTemp(String playerName) {
        for (String[] strings : createTeamTemp) {
            if (strings[0].equals(playerName)) {
                createTeamTemp.remove(strings);
                return;
            }
        }
    }

    // playerHasAction
    public boolean containCreationTeamTemp(String playerName) {
        if (createTeamTemp.isEmpty()) return false;
        for (String[] strings : createTeamTemp) {
            if (strings[0].equals(playerName)) {
                return true;
            }
        }
        return false;
    }


    // getPlayerActionName
    public String getPlayerActions(String playerName) {
        return playersTempActions.get(playerName);
    }

    // removePlayerAction
    public void removePlayerTempAction(String playerName) {
        playersTempActions.remove(playerName);
    }

    // playerHasActionName
    public boolean containPlayerTempAction(String playerName, String actionName) {
        if (playersTempActions.isEmpty()) return false;
        if (playersTempActions.containsKey(playerName) && playersTempActions.get(playerName).equals(actionName)) {
            return true;
        }else{
            return false;
        }
    }

    // containsStorage
    public boolean containTeamStorage(String teamId, String storageId) {
        if (storageTemp.isEmpty()) return false;
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[2].equals(storageId)) {
                return true;
            }
        }
        return false;
    }

    // addStorage
    public void addTeamStorage(String teamId, Inventory inventoryInstance, String storageId, String content) {
        storageTemp.add(new Object[]{teamId, inventoryInstance, storageId, content});
    }

    // removeStorage
    public void removeTeamStorage(String teamId, String storageId) {
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[2].equals(storageId)) {
                storageTemp.remove(objects);
                return;
            }
        }
    }

    // replaceStorage
    public void replaceTeamStorage(String teamId, Inventory inventoryInstance, String storageId, String content) {
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[2].equals(storageId)) {
                storageTemp.remove(objects);
                storageTemp.add(new Object[]{teamId, inventoryInstance, storageId, content});
                return;
            }
        }
    }

    // getStorage
    public String getTeamStorageContent(String teamId, String storageId) {
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[2].equals(storageId)) {
                return (String) objects[3];
            }
        }
        return null;
    }

    // Storage.getStorageInstance
    public Inventory getStorageInstance(String teamId, String storageId) {
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[2].equals(storageId)) {
                return (Inventory) objects[1];
            }
        }
        return null;
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

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public SQLManager getSQLManager() {
        return sqlManager;
    }

    public SQLiteManager getSQLiteManager() {
        return sqliteManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ColorsBuilder getColorsBuilder() {
        return colorsBuilder;
    }

    public ConversationBuilder getConversationBuilder() {
        return conversationBuilder;
    }

    public InventoryBuilder getInventoryBuilder() {
        return inventoryBuilder;
    }

    public TeamMembersUtils getTeamMembersUtils() {
        return teamMembersUtils;
    }

    public SerializeManager getSerializeManager() {
        return serializeManager;
    }
}

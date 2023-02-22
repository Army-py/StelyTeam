package fr.army.stelyteam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import fr.army.stelyteam.commands.CommandManager;
import fr.army.stelyteam.events.InventoryClickManager;
import fr.army.stelyteam.events.InventoryCloseManager;
import fr.army.stelyteam.events.PlayerQuit;
import fr.army.stelyteam.events.inventories.AdminInventory;
import fr.army.stelyteam.events.inventories.CreateTeamInventory;
import fr.army.stelyteam.events.inventories.MemberInventory;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.serializer.ItemStackSerializer;

public class StelyTeamPlugin extends JavaPlugin {

    private static StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private YamlConfiguration messages;
    private CacheManager cacheManager;
    private SQLiteDataManager sqliteManager;
    private EconomyManager economyManager;
    private CommandManager commandManager;
    private MessageManager messageManager;
    private ColorsBuilder colorsBuilder;
    private ConversationBuilder conversationBuilder;
    private InventoryBuilder inventoryBuilder;
    private ItemStackSerializer serializeManager;
    private DatabaseManager databaseManager;


    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();

        this.config = initFile(this.getDataFolder(), "config.yml");
        this.messages = initFile(this.getDataFolder(), "messages.yml");

        // this.sqlManager = new DatabaseManager(this);
        this.sqliteManager = new SQLiteDataManager(this);

        try {
            // this.sqlManager.connect();
            // this.sqliteManager.connect();
            this.databaseManager = DatabaseManager.connect(this);
            this.databaseManager.createTables();
            this.getLogger().info("SQL connectée au plugin !");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
        
        this.cacheManager = new CacheManager();
        // this.sqlManager.createTables();
        // this.sqliteManager.createTables();
        this.economyManager = new EconomyManager(this);
        this.messageManager = new MessageManager(this);
        this.commandManager = new CommandManager(this);
        this.colorsBuilder = new ColorsBuilder(this);
        this.conversationBuilder = new ConversationBuilder(this);
        this.inventoryBuilder = new InventoryBuilder(this);
        this.serializeManager = new ItemStackSerializer();
        
        getServer().getPluginManager().registerEvents(new InventoryClickManager(this), this);
        getServer().getPluginManager().registerEvents(new InventoryCloseManager(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);

        getLogger().info("StelyTeam ON");
    }


    @Override
    public void onDisable() {
        // sqlManager.disconnect();
        // sqliteManager.disconnect();
        this.databaseManager.disconnect();
        getLogger().info("StelyTeam OFF");
    }


    public static StelyTeamPlugin getPlugin() {
        return plugin;
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


    public void openMainInventory(Player player, Team team){
        String playerName = player.getName();

        if (team == null){
            // inventory = inventoryBuilder.createTeamInventory();
            new CreateTeamInventory(player).openMenu();
        }else if(team.isTeamOwner(playerName)){
            // inventory = inventoryBuilder.createAdminInventory();
            new AdminInventory(player).openMenu();
        }else if (plugin.playerHasPermissionInSection(playerName, team, "manage")){
            // inventory = inventoryBuilder.createAdminInventory();
            new AdminInventory(player).openMenu();
        }else{
            // inventory = inventoryBuilder.createMemberInventory(playerName, team);
            new MemberInventory(player).openMenu(team);
        }
        // player.openInventory(inventory);
    }


    public String getRankFromId(Integer value) {
        for (String key : config.getConfigurationSection("ranks").getKeys(false)) {
            if (config.getInt("ranks." + key + ".id") == value) {
                return key;
            }
        }
        return null;
    }


    public String getStorageFromId(Integer storageId){
        for (String key : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)) {
            if (config.getInt("inventories.storageDirectory." + key + ".storageId") == storageId) {
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


    public Integer getMinStorageId(){
        Integer minStorageId = Integer.MAX_VALUE;
        for (String key : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)) {
            if (config.getInt("inventories.storageDirectory." + key + ".storageId") < minStorageId) {
                if (key.equals("close")) continue;
                minStorageId = config.getInt("inventories.storageDirectory." + key + ".storageId");
            }
        }
        return minStorageId;
    }


    public boolean playerHasPermission(String playerName, Team team, String permissionName){
        Integer permissionRank = team.getPermissionRank(permissionName);
        if (permissionRank != null){
            return permissionRank >= team.getMemberRank(playerName);
        }

        String rankPath = config.getString("inventories.permissions."+permissionName+".rankPath");
        if (team.isTeamOwner(playerName) || config.getInt("inventories."+rankPath+".rank") == -1){
            return true;
        }else if (config.getInt("inventories."+rankPath+".rank") >= team.getMemberRank(playerName)){
            return true;
        }else if (permissionName.equals("close") || permissionName.equals("leaveTeam") || permissionName.equals("teamInfos")){
            return true;
        }
        return false;
    }


    public boolean playerHasPermissionInSection(String playerName, Team team, String sectionName){
        for (String section : config.getConfigurationSection("inventories." + sectionName).getKeys(false)){
            if (playerHasPermission(playerName, team, section) && !section.equals("close")){
                return true;
            }
        }
        return false;
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

    // public DatabaseManager getSQLManager() {
    //     return sqlManager;
    // }

    public SQLiteDataManager getSQLiteManager() {
        return sqliteManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
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

    public ItemStackSerializer getSerializeManager() {
        return serializeManager;
    }
}



/*
 * 
 * Problème à regler :
 *  - si il y a un changement de nom de team alors qu'un stockage est dans le cache
 */
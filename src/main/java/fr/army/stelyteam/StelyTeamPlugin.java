package fr.army.stelyteam;

import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.cache.TeamCache;
import fr.army.stelyteam.chat.TeamChatLoader;
import fr.army.stelyteam.chat.TeamChatManager;
import fr.army.stelyteam.command.CommandManager;
import fr.army.stelyteam.repository.EMFLoader;
import fr.army.stelyteam.external.ExternalManager;
import fr.army.stelyteam.listener.ListenerLoader;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.impl.AdminMenu;
import fr.army.stelyteam.menu.impl.CreateTeamMenu;
import fr.army.stelyteam.menu.impl.MemberMenu;
import fr.army.stelyteam.repository.exception.RepositoryException;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.team.TeamManager;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;
import fr.army.stelyteam.utils.manager.serializer.ItemStackSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Objects;

public class StelyTeamPlugin extends JavaPlugin {

    private static StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private YamlConfiguration messages;
    private EMFLoader emfLoader;
    private CacheManager cacheManager;
    private SQLiteDataManager sqliteManager;
    private EconomyManager economyManager;
    private StorageManager storageManager;
    private TeamCache teamCache;
    private TeamManager teamManager;
    private TeamChatManager teamChatManager;
    private CommandManager commandManager;
    private MessageManager messageManager;
    private ColorsBuilder colorsBuilder;
    private ConversationBuilder conversationBuilder;
    private ItemStackSerializer serializeManager;
    private DatabaseManager databaseManager;
    private ExternalManager externalManager;


    @Override
    public void onEnable() {
        plugin = this;

        this.config = initFile(this.getDataFolder(), "config.yml");
        this.messages = initFile(this.getDataFolder(), "messages.yml");

        this.emfLoader = new EMFLoader();
        this.emfLoader.setupEntityManagerFactory();

        this.sqliteManager = new SQLiteDataManager(this);

        try {
            this.databaseManager = DatabaseManager.connect(this);
            this.sqliteManager.connect();
            this.sqliteManager.createTables();
            this.databaseManager.createTables();
            this.getLogger().info("SQL connectée au plugin !");
        } catch (ClassNotFoundException | SQLException e) {
            getLogger().severe(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        this.cacheManager = new CacheManager();
        this.economyManager = new EconomyManager(this);
        this.messageManager = new MessageManager(this);

        try {
            storageManager = new StorageManager(emfLoader.getEntityManager());
        } catch (RepositoryException e) {
            getLogger().severe(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        teamCache = new TeamCache(storageManager);
        teamManager = new TeamManager(storageManager, teamCache);
        final TeamChatLoader teamChatLoader = new TeamChatLoader();
        this.teamChatManager = teamChatLoader.load();
        this.commandManager = new CommandManager(this);
        this.colorsBuilder = new ColorsBuilder(this);
        this.conversationBuilder = new ConversationBuilder(this);
        this.serializeManager = new ItemStackSerializer();


        this.externalManager = new ExternalManager();
        this.externalManager.load();

        final ListenerLoader listenerLoader = new ListenerLoader();
        listenerLoader.registerListeners(this);

        getLogger().info("StelyTeam ON");
    }


    @Override
    public void onDisable() {
        this.externalManager.unload();
        
        closeAllPlayerInventories();

        this.databaseManager.disconnect();
        this.sqliteManager.disconnect();
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


    private void closeAllPlayerInventories(){
        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof TeamMenu){
                player.closeInventory();
            }
        }
    }


    public void openMainInventory(Player player, Team team){
        String playerName = player.getName();

        if (team == null){
            new CreateTeamMenu(player).openMenu();
        }else if(team.isTeamOwner(playerName)){
            new AdminMenu(player).openMenu();
        }else if (plugin.playerHasPermissionInSection(playerName, team, "manage")
            || plugin.playerHasPermissionInSection(playerName, team, "editMembers")
            || plugin.playerHasPermissionInSection(playerName, team, "editAlliances")){
            new AdminMenu(player).openMenu();
        }else{
            new MemberMenu(player).openMenu(team);
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
        if (permissionName.equals("close")) return true;

        Integer permissionRank = team.getPermissionRank(permissionName);

        if (permissionRank != null){
            return permissionRank >= team.getMemberRank(playerName);
        }

        if (team.isTeamOwner(playerName) || config.getInt("inventories.permissions."+permissionName+".rank") == -1){
            return true;
        }else if (config.getInt("inventories.permissions."+permissionName+".rank") >= team.getMemberRank(playerName)){
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

    public EMFLoader getEmfLoader() {
        return emfLoader;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

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

    public ItemStackSerializer getSerializeManager() {
        return serializeManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public TeamChatManager getTeamChatManager() {
        return teamChatManager;
    }

    public TeamCache getTeamCache() {
        return teamCache;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }
}
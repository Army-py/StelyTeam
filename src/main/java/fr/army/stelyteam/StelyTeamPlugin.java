package fr.army.stelyteam;

import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.cache.TeamCache;
import fr.army.stelyteam.chat.TeamChatLoader;
import fr.army.stelyteam.chat.TeamChatManager;
import fr.army.stelyteam.command.CommandManager;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.external.ExternalManager;
import fr.army.stelyteam.listener.ListenerLoader;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.repository.EMFLoader;
import fr.army.stelyteam.repository.exception.RepositoryException;
import fr.army.stelyteam.team.TeamManager;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.loader.ConfigLoader;
import fr.army.stelyteam.utils.loader.exception.UnableLoadConfigException;
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

import java.sql.SQLException;


// TODO: Remplacer la conversation pour promouvoir un membre owner par un menu
// TODO: Ajouter un message de téléportation lorsqu'un membre se tp au home de team
// TODO: Ajouter des sons sur les boutons
// TODO: Maybe ajouter un son lors de la téléportation au home de team ?
// TODO: rajouter une commande pour modifier la description d'une team
// TODO: mettre de l'ordre dans onEnable()
// TODO: changer le type des dates dans la bdd
// TODO: ajouter un menu avec toutes les demandes d'alliance, au lieu d'attendre qu'un owner soit co


public class StelyTeamPlugin extends JavaPlugin {

    private static boolean debug = false;

    private String currentServerName;
    private String[] serverNames;
    private String teamChatFormat;

    private static StelyTeamPlugin plugin;
    private ConfigLoader configLoader;
    private Config config;
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

        this.configLoader = new ConfigLoader(this);

        try {
            this.config = new Config(this.configLoader.initFile("config.yml"));
        } catch (UnableLoadConfigException e) {
            getLogger().severe("Unable to load config.yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.config.load();
        Config.language = Config.language == null ? "en_us" : Config.language;

        try {
            this.messages = this.configLoader.initFile(Config.language + ".yml");
        } catch (UnableLoadConfigException e) {
            getLogger().severe("Unable to load messages.yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.currentServerName = Bukkit.getServer().getMotd();
        this.serverNames = Config.serverNames.toArray(new String[0]);
        this.teamChatFormat = Config.teamChatFormat;

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
        
        this.serializeManager = new ItemStackSerializer();
        this.cacheManager = new CacheManager(this);
        this.economyManager = new EconomyManager();
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
        this.economyManager = new EconomyManager();
        this.teamChatManager = teamChatLoader.load();
        this.commandManager = new CommandManager(this);
        this.colorsBuilder = new ColorsBuilder(this);
        this.conversationBuilder = new ConversationBuilder(this);


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


    private void closeAllPlayerInventories(){
        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof TeamMenuOLD){
                player.closeInventory();
            }
        }
    }


    // public void openMainInventory(Player player, Team team){
    //     String playerName = player.getName();
    //
    //     if (team == null){
    //         new CreateTeamMenu(player, null).openMenu();
    //     }else if(team.isTeamOwner(playerName)){
    //         new AdminMenu(player, null).openMenu();
    //     }else if (playerHasPermissionInSection(playerName, team, "manage")
    //         || playerHasPermissionInSection(playerName, team, "editMembers")
    //         || playerHasPermissionInSection(playerName, team, "editAlliances")
    //         || playerHasPermission(playerName, team, "teamList")){
    //         new AdminMenu(player, null).openMenu();
    //     }else{
    //         new MemberMenu(player, null).openMenu();
    //     }
    // }


    // public String getRankFromId(Integer value) {
    //     for (String key : config.getConfigurationSection("ranks").getKeys(false)) {
    //         if (config.getInt("ranks." + key + ".id") == value) {
    //             return key;
    //         }
    //     }
    //     return null;
    // }


    // public String getStorageFromId(Integer storageId){
    //     for (String key : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)) {
    //         if (config.getInt("inventories.storageDirectory." + key + ".storageId") == storageId) {
    //             return key;
    //         }
    //     }
    //     return null;
    // }


    // public Integer getLastRank(){
    //     Integer lastRank = 0;
    //     for (String key : config.getConfigurationSection("ranks").getKeys(false)) {
    //         if (config.getInt("ranks." + key + ".id") > lastRank) {
    //             lastRank = config.getInt("ranks." + key + ".id");
    //         }
    //     }
    //     return lastRank;
    // }
    //
    //
    // public Integer getMinStorageId(){
    //     Integer minStorageId = Integer.MAX_VALUE;
    //     for (String key : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)) {
    //         if (config.getInt("inventories.storageDirectory." + key + ".storageId") < minStorageId) {
    //             if (key.equals("close")) continue;
    //             minStorageId = config.getInt("inventories.storageDirectory." + key + ".storageId");
    //         }
    //     }
    //     return minStorageId;
    // }


    // public boolean playerHasPermission(String playerName, Team team, String permissionName){
    //     if (permissionName.equals("close")) return true;
    //
    //     Integer permissionRank = team.getPermissionRank(permissionName);
    //
    //     if (permissionRank != null){
    //         return permissionRank >= team.getMemberRank(playerName);
    //     }
    //
    //     if (team.isTeamOwner(playerName) || config.getInt("inventories.permissions."+permissionName+".rank") == -1){
    //         return true;
    //     }else if (config.getInt("inventories.permissions."+permissionName+".rank") >= team.getMemberRank(playerName)){
    //         return true;
    //     }
    //     return false;
    // }
    //
    //
    // public boolean playerHasPermissionInSection(String playerName, Team team, String sectionName){
    //     if (sectionName.equals("close")) return true;
    //     for (String section : config.getConfigurationSection("inventories." + sectionName).getKeys(false)){
    //         if (playerHasPermission(playerName, team, section) && !section.equals("close")){
    //             return true;
    //         }
    //     }
    //     return false;
    // }
    //
    //
    // public Set<UUID> getAllowedPlayers(String permissionName){
    //     final Set<UUID> players = new HashSet<UUID>();
    //     for (Player player : Bukkit.getOnlinePlayers()){
    //         if (player.hasPermission(permissionName)){
    //             players.add(player.getUniqueId());
    //         }
    //     }
    //     return players;
    // }


    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public Config getConfiguration() {
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

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public String[] getServerNames(){
        return serverNames;
    }

    public String getCurrentServerName(){
        return currentServerName;
    }

    public String getTeamChatFormat(){
        return teamChatFormat;
    }


    public void toggleDebug(){
        debug = !debug;
    }

    public boolean isDebug() {
        return debug;
    }

}
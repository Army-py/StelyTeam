package fr.army.stelyteam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import fr.army.stelyteam.commands.CommandManager;
import fr.army.stelyteam.events.InventoryClickManager;
import fr.army.stelyteam.events.InventoryCloseManager;
import fr.army.stelyteam.events.PlayerQuit;
import fr.army.stelyteam.utils.TeamMembersUtils;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;
import fr.army.stelyteam.utils.manager.SQLiteManager;
import fr.army.stelyteam.utils.manager.SerializeManager;

public class StelyTeamPlugin extends JavaPlugin {

    private static StelyTeamPlugin plugin;
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


    @Override
    public void onEnable() {
        plugin = this;
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
        sqlManager.disconnect();
        sqliteManager.disconnect();
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


    public boolean playerHasPermission(String playerName, String teamId, String permission){
        Integer permissionRank = sqlManager.getRankAssignement(teamId, permission);
        if (permissionRank != null){
            return permissionRank >= sqlManager.getMemberRank(playerName);
        }

        String rankPath = config.getString("inventories.permissions."+permission+".rankPath");
        if (sqlManager.isOwner(playerName) || config.getInt("inventories."+rankPath+".rank") == -1){
            return true;
        }else if (config.getInt("inventories."+rankPath+".rank") >= sqlManager.getMemberRank(playerName)){
            return true;
        }else if (permission.equals("close") || permission.equals("leaveTeam") || permission.equals("teamInfos")){
            return true;
        }
        return false;
    }


    public boolean playerHasPermissionInSection(String playerName, String teamName, String sectionName){
        for (String section : config.getConfigurationSection("inventories." + sectionName).getKeys(false)){
            if (playerHasPermission(playerName, teamName, section) && !section.equals("close")){
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

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
import fr.army.stelyteam.utils.ColorsBuilder;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.InventoryBuilder;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;
import fr.army.stelyteam.utils.SQLiteManager;
import fr.army.stelyteam.utils.SerializeManager;
import fr.army.stelyteam.utils.TeamMembersUtils;
import fr.army.stelyteam.utils.conversation.ConversationBuilder;

public class StelyTeamPlugin extends JavaPlugin {

    private YamlConfiguration config;
    private YamlConfiguration messages;
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


    // public NBTTagCompound getNBTTag(ItemStack bukkitStack) {
    //     net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(bukkitStack);
    //     NBTTagCompound nbtTagCompound = itemStack.getTag() != null ? itemStack.getTag() : new NBTTagCompound();
    //     return nbtTagCompound;
    // }


    // public ItemStack getFromNBTTag(ItemStack bukkitStack, NBTTagCompound nbtTagCompound) {
    //     net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(bukkitStack);
    //     itemStack.setTag(nbtTagCompound);
    //     return CraftItemStack.asBukkitCopy(itemStack);
    // }


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


    public boolean containTeamStorage(String teamId, String storageId) {
        if (storageTemp.isEmpty()) return false;
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[2].equals(storageId)) {
                return true;
            }
        }
        return false;
    }


    public boolean teamStorageHasContent(String teamId, String storageId) {
        if (storageTemp.isEmpty()) return false;
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[2].equals(storageId) && !((String) objects[3]).isEmpty()) {
                return true;
            }
        }
        return false;
    }


    public boolean containLastPlayer(String teamId, String playerName) {
        if (storageTemp.isEmpty()) return false;
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[1].equals(playerName)) {
                return true;
            }
        }
        return false;
    }


    public void addTeamStorage(String teamId, Inventory inventoryInstance, String storageId, String content) {
        storageTemp.add(new Object[]{teamId, inventoryInstance, storageId, content});
    }


    public void removeTeamStorage(String teamId, String storageId) {
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[2].equals(storageId)) {
                storageTemp.remove(objects);
                return;
            }
        }
    }


    public void replaceTeamStorage(String teamId, Inventory inventoryInstance, String storageId, String content) {
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[2].equals(storageId)) {
                storageTemp.remove(objects);
                storageTemp.add(new Object[]{teamId, inventoryInstance, storageId, content});
                return;
            }
        }
    }


    public String getTeamStorageContent(String teamId, String storageId) {
        for (Object[] objects : storageTemp) {
            if (objects[0].equals(teamId) && objects[2].equals(storageId)) {
                return (String) objects[3];
            }
        }
        return null;
    }


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

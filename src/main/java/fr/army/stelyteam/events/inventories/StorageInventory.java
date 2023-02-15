package fr.army.stelyteam.events.inventories;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Storage;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.serializer.ItemStackSerializer;

public class StorageInventory {
    
    private InventoryClickEvent clickEvent;
    private InventoryCloseEvent closeEvent;
    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private YamlConfiguration config;
    private InventoryBuilder inventoryBuilder;
    private DatabaseManager sqlManager;
    private ItemStackSerializer serializeManager;


    public StorageInventory(InventoryClickEvent clickEvent, StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.clickEvent = clickEvent;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getDatabaseManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
        this.serializeManager = plugin.getSerializeManager();
    }

    public StorageInventory(InventoryCloseEvent closeEvent, StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.closeEvent = closeEvent;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getDatabaseManager();
        this.serializeManager = plugin.getSerializeManager();
    }


    public void onInventoryClick(){
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        Team team = sqlManager.getTeamFromPlayerName(playerName);
        Integer storageId = getStorageId(clickEvent.getView().getTitle());
        String itemName;
        
        if (clickEvent.getCurrentItem() != null){
            Material material = clickEvent.getCurrentItem().getType();
            itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
            if (material.name().equals(config.getString("emptyCase"))){
                clickEvent.setCancelled(true);
                return;
            }else if (itemName.equals(config.getString("inventories.storage.previous.itemName"))){
                clickEvent.setCancelled(true);
                player.openInventory(inventoryBuilder.createStorageInventory(team, storageId-1));
                return;
            }else if (itemName.equals(config.getString("inventories.storage.next.itemName"))){
                clickEvent.setCancelled(true);
                player.openInventory(inventoryBuilder.createStorageInventory(team, storageId+1));
            }else{
                if (itemName.equals(config.getString("inventories.storage.close.itemName"))){
                    clickEvent.setCancelled(true);
                    if (clickEvent.getCursor().getType().equals(Material.AIR)){
                        player.openInventory(inventoryBuilder.createStorageDirectoryInventory(playerName, team));
                        return;
                    }else return;
                }
            }
        }
        clickEvent.setCancelled(false);
    }


    public void onInventoryClose(){
        Player player = (org.bukkit.entity.Player) closeEvent.getPlayer();
        Inventory storageInventory = closeEvent.getInventory();
        String playerName = player.getName();
        Team team = sqlManager.getTeamFromPlayerName(playerName);
        Integer storageId = getStorageId(closeEvent.getView().getTitle());
        ItemStack[] inventoryContent = closeEvent.getInventory().getContents();
        Storage storage;

        if (cacheManager.containsStorage(team.getTeamName(), storageId)){
            storage = cacheManager.replaceStorageContent(team.getTeamName(), storageId, serializeManager.serializeToByte(inventoryContent));
        }else{
            storage = new Storage(team, storageId, storageInventory, serializeManager.serializeToByte(inventoryContent));
        }

        // byte[] inventoryContentString = storage.getStorageContent();
        // if (!sqlManager.teamHasStorage(teamId, storageId)){
        //     if (!sqlManager.storageIdExist(storageId)){
        //         sqlManager.insertStorageId(storageId);
        //     }
        //     sqlManager.insertStorageContent(teamId, storageId, inventoryContentString);
        // }else{
        //     sqlManager.updateStorageContent(teamId, storageId, inventoryContentString);
        // }
        
        storage.saveStorageToCache();
        storage.saveStorageToDatabase();
    }


    private Integer getStorageId(String inventoryTitle){
        for(String str : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
            if (config.getString(config.getString("inventories.storageDirectory." + str + ".itemName")).equals(inventoryTitle)){
                return config.getInt("inventories.storageDirectory." + str + ".storageId");
            }
        }
        return null;
    }
}

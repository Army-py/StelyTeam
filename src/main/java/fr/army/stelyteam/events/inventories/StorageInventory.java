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
import fr.army.stelyteam.utils.manager.SQLManager;
import fr.army.stelyteam.utils.manager.SerializeManager;

public class StorageInventory {
    
    private InventoryClickEvent clickEvent;
    private InventoryCloseEvent closeEvent;
    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private YamlConfiguration config;
    private InventoryBuilder inventoryBuilder;
    private SQLManager sqlManager;
    private SerializeManager serializeManager;


    public StorageInventory(InventoryClickEvent clickEvent, StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.clickEvent = clickEvent;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
        this.serializeManager = plugin.getSerializeManager();
    }

    public StorageInventory(InventoryCloseEvent closeEvent, StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.closeEvent = closeEvent;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.serializeManager = plugin.getSerializeManager();
    }


    public void onInventoryClick(){
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName;
        
        // Fermeture ou retour en arrière de l'inventaire
        if (clickEvent.getCurrentItem() != null){
            itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
            if (itemName.equals(config.getString("inventories.storage.close.itemName"))){
                clickEvent.setCancelled(true);
                if (clickEvent.getCursor().getType().equals(Material.AIR)){
                    player.openInventory(inventoryBuilder.createStorageDirectoryInventory(playerName));
                    return;
                }else return;
            }
        }
        clickEvent.setCancelled(false);
    }


    public void onInventoryClose(){
        Player player = (org.bukkit.entity.Player) closeEvent.getPlayer();
        Inventory storageInventory = closeEvent.getInventory();
        String playerName = player.getName();
        String teamId = sqlManager.getTeamNameFromPlayerName(playerName);
        Team team = sqlManager.getTeamFromPlayerName(playerName);
        Integer storageId = getStorageId(closeEvent.getView().getTitle());
        ItemStack[] inventoryContent = closeEvent.getInventory().getContents();
        int closeButtonSlot = config.getInt("inventories.storage.close.slot");
        inventoryContent[closeButtonSlot] = null;
        Storage storage = new Storage(team, storageId, storageInventory, serializeManager.serialize(inventoryContent));

        // if (plugin.containTeamStorage(teamId, storageId.toString())){
        //     plugin.replaceTeamStorage(teamId, storageInventory, storageId.toString(), serializeManager.serialize(inventoryContent));
        // }else{
        //     plugin.addTeamStorage(teamId, storageInventory, storageId.toString(), serializeManager.serialize(inventoryContent));
        // }
        if (cacheManager.containsStorage(team, storageId)){
            cacheManager.replaceStorage(storage);
        }else{
            cacheManager.addStorage(storage);
        }

        // if (plugin.containTeamStorage(teamId, storageId.toString())){
        if (cacheManager.containsStorage(team, storageId)){
            // String inventoryContentString = plugin.getTeamStorageContent(teamId, storageId.toString());
            String inventoryContentString = serializeManager.serialize(inventoryContent);
            if (!sqlManager.teamHasStorage(teamId, storageId)){
                if (!sqlManager.storageExist(storageId)){
                    sqlManager.insertStorage(storageId);
                }
                sqlManager.insertStorageContent(teamId, storageId, inventoryContentString);
            }else{
                sqlManager.updateStorageContent(teamId, storageId, inventoryContentString);
            }
        }
    }


    private Integer getStorageId(String inventoryTitle){
        Integer storageId;
        for(String str : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
            if (config.getString(config.getString("inventories.storageDirectory." + str + ".itemName")).equals(inventoryTitle)){
                storageId = config.getInt("inventories.storageDirectory." + str + ".storageId");
                return storageId;
            }
        }
        return null;
    }
}

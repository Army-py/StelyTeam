package fr.army.stelyteam.events.inventories;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryBuilder;
import fr.army.stelyteam.utils.SQLManager;
import fr.army.stelyteam.utils.SerializeManager;

public class StorageInventory {
    
    private InventoryClickEvent clickEvent;
    private InventoryCloseEvent closeEvent;
    private YamlConfiguration config;
    private InventoryBuilder inventoryBuilder;
    private SQLManager sqlManager;
    private SerializeManager serializeManager;
    private StelyTeamPlugin plugin;


    public StorageInventory(InventoryClickEvent clickEvent, StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.clickEvent = clickEvent;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
        this.serializeManager = plugin.getSerializeManager();
    }

    public StorageInventory(InventoryCloseEvent closeEvent, StelyTeamPlugin plugin) {
        this.plugin = plugin;
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
        String teamId = sqlManager.getTeamIDFromPlayer(playerName);
        String storageId = getStorageId(closeEvent.getView().getTitle());
        ItemStack[] inventoryContent = closeEvent.getInventory().getContents();
        int closeButtonSlot = config.getInt("inventories.storage.close.slot");

        inventoryContent[closeButtonSlot] = null;
        if (plugin.containTeamStorage(teamId, storageId)){
            plugin.replaceTeamStorage(teamId, storageInventory, storageId, serializeManager.serialize(inventoryContent));
        }else{
            plugin.addTeamStorage(teamId, storageInventory, storageId, serializeManager.serialize(inventoryContent));
        }
        
        if (plugin.containTeamStorage(teamId, storageId)){
            String inventoryContentString = plugin.getTeamStorageContent(teamId, storageId);
            if (!sqlManager.teamHasStorage(teamId, storageId)){
                sqlManager.insertStorageContent(teamId, storageId, inventoryContentString);
            }else{
                sqlManager.updateStorageContent(teamId, storageId, inventoryContentString);
            }
        }
    }


    private String getStorageId(String inventoryTitle){
        String storageId;
        for(String str : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
            if (config.getString(config.getString("inventories.storageDirectory." + str + ".itemName")).equals(inventoryTitle)){
                storageId = config.getString("inventories.storageDirectory." + str + ".storageId");
                return storageId;
            }
        }
        return null;
    }
}

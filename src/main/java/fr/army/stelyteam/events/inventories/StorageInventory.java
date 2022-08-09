package fr.army.stelyteam.events.inventories;

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

    public StorageInventory(InventoryClickEvent clickEvent, StelyTeamPlugin plugin) {
        this.clickEvent = clickEvent;
        this.config = plugin.getConfig();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }

    public StorageInventory(InventoryCloseEvent closeEvent, StelyTeamPlugin plugin) {
        this.closeEvent = closeEvent;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.serializeManager = plugin.getSerializeManager();
    }

    public void onInventoryClick(){
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();

        // Fermeture ou retour en arrière de l'inventaire
        if (itemName.equals(config.getString("inventories.storage.close.itemName"))){
            Inventory inventory = inventoryBuilder.createStorageDirectoryInventory(playerName);
            player.openInventory(inventory);
        }else{
            clickEvent.setCancelled(false);
        }
    }


    public void onInventoryClose(){
        Player player = (org.bukkit.entity.Player) closeEvent.getPlayer();
        String playerName = player.getName();
        String teamId = sqlManager.getTeamIDFromPlayer(playerName);
        String storageId = getStorageId(closeEvent.getView().getTitle());
        ItemStack[] inventoryContents = closeEvent.getInventory().getContents();
        int closeButtonSlot = config.getInt("inventories.storage.close.slot");

        if (!sqlManager.teamHasStorage(teamId, storageId)){
            if (getStoredItemCount(inventoryContents) > config.getConfigurationSection("inventories.storage").getKeys(false).size()){
                inventoryContents[closeButtonSlot] = null;
                sqlManager.insertStorageContent(teamId, storageId, serializeManager.itemStackToBase64(inventoryContents));
            }
        }else{
            inventoryContents[closeButtonSlot] = null;
            sqlManager.updateStorageContent(teamId, storageId, serializeManager.itemStackToBase64(inventoryContents));
        }
    }


    private String getStorageId(String inventoryTitle){
        String storageId;
        for(String str : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
            if (config.getString("inventories.storageDirectory." + str + ".itemName").equals(inventoryTitle)){
                storageId = config.getString("inventories.storageDirectory." + str + ".storageId");
                return storageId;
            }
        }
        return null;
    }

    private int getStoredItemCount(ItemStack[] inventoryContents){
        int storedItemsCount = 0;
        for (int i = 0; i < inventoryContents.length; i++) {
            if (inventoryContents[i] != null){
                storedItemsCount++;
            }
        }
        return storedItemsCount;
    }
}

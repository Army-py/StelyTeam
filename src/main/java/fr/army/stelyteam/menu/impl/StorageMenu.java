package fr.army.stelyteam.menu.impl;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.PagedMenu;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.serializer.ItemStackSerializer;


public class StorageMenu extends PagedMenu {

    final CacheManager cacheManager = plugin.getCacheManager();
    final ItemStackSerializer serializeManager = plugin.getSerializeManager();

    public StorageMenu(Player viewer, TeamMenu previousMenu) {
        super(
            viewer,
            Menus.STORAGE_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory(int storageId) {
        // String inventoryName = config.getString(config.getString("inventories.storageDirectory."+plugin.getStorageFromId(storageId)+".itemName"));
        String inventoryName = Menus.getStorageMenuName(storageId);
        UUID teamUuid = team.getTeamUuid();
        Inventory inventory;

        if (cacheManager.containsStorage(teamUuid, storageId)){
            inventory = cacheManager.getStorage(teamUuid, storageId).getInventoryInstance();
        }else{
            inventory = Bukkit.createInventory(this, this.menuSlots, inventoryName);

            if (team.hasStorage(storageId)){
                byte[] contentBytes = team.getStorage(storageId).getStorageContent();
                ItemStack[] content = serializeManager.deserializeFromByte(contentBytes);
                inventory.setContents(content);
            }
        }

        emptyCases(inventory, config.getIntegerList("inventories.storage.emptyCase.slots"));

        for(String buttonName : config.getConfigurationSection("inventories.storage").getKeys(false)){
            Integer slot = config.getInt("inventories.storage."+buttonName+".slot");
            Material material = Material.getMaterial(config.getString("inventories.storage."+buttonName+".itemType"));
            String displayName = config.getString("inventories.storage."+buttonName+".itemName");
            List<String> lore = config.getStringList("inventories.storage."+buttonName+".lore");
            String headTexture = config.getString("inventories.storage."+buttonName+".headTexture");

            if (buttonName.equals("previous")){
                if (storageId == plugin.getMinStorageId()) continue;
            }else if (buttonName.equals("next")){
                if (storageId == team.getTeamStorageLvl()) continue;
            }else if (buttonName.equals("emptyCase")){
                continue;
            }

            inventory.setItem(slot, ItemBuilder.getItem(material, buttonName, displayName, lore, headTexture, false));
        }

        return inventory;
    }


    @Override
    public void openMenu(int storageId){
        this.open(createInventory(storageId));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        Integer storageId = getStorageId(clickEvent.getView().getTitle());
        
        if (clickEvent.getCurrentItem() != null){
            if (Buttons.PREVIOUS_STORAGE_BUTTON.isClickedButton(clickEvent)){
                clickEvent.setCancelled(true);
                new StorageMenu(player, previousMenu).openMenu(storageId-1);
                return;
            }else if (Buttons.NEXT_STORAGE_BUTTON.isClickedButton(clickEvent)){
                clickEvent.setCancelled(true);
                new StorageMenu(player, previousMenu).openMenu(storageId+1);
            }else{
                if (Buttons.CLOSE_STORAGE_MENU_BUTTON.isClickedButton(clickEvent)){
                    clickEvent.setCancelled(true);
                    if (clickEvent.getCursor().getType().equals(Material.AIR)){
                        // player.openInventory(inventoryBuilder.createStorageDirectoryInventory(team));
                        // new StorageDirectoryMenu(player, this).openMenu();
                        previousMenu.setViewer(player);
                        previousMenu.openMenu();
                        return;
                    }else return;
                }
            }
        }
        clickEvent.setCancelled(false);
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {
        Player player = (org.bukkit.entity.Player) closeEvent.getPlayer();
        Inventory storageInventory = closeEvent.getInventory();
        Team team = Team.init(player);
        UUID teamUuid = team.getTeamUuid();
        Integer storageId = getStorageId(closeEvent.getView().getTitle());
        ItemStack[] inventoryContent = closeEvent.getInventory().getContents();
        Storage storage;

        if (cacheManager.containsStorage(teamUuid, storageId)){
            storage = cacheManager.replaceStorageContent(teamUuid, storageId, serializeManager.serializeToByte(inventoryContent));
        }else if (team.hasStorage(storageId)){
            storage = team.getStorage(storageId);
            storage.setStorageInstance(storageInventory);
            storage.setStorageContent(serializeManager.serializeToByte(inventoryContent));
        }else{
            storage = new Storage(teamUuid, storageId, storageInventory, serializeManager.serializeToByte(inventoryContent), null);
        }
        
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


    @Override
    public void openMenu() {}
}
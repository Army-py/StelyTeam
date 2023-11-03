package fr.army.stelyteam.menu.impl.old;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.menu.MenusOLD;
import fr.army.stelyteam.menu.PagedMenuOLD;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.team.Storage;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.ItemBuilderOLD;
import fr.army.stelyteam.utils.manager.serializer.ItemStackSerializer;


public class StorageMenu extends PagedMenuOLD {

    private final ItemStackSerializer serializeManager = plugin.getSerializeManager();

    public StorageMenu(Player viewer, TeamMenuOLD previousMenu) {
        super(
            viewer,
            MenusOLD.STORAGE_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory(int storageId) {
        // String inventoryName = config.getString(config.getString("inventories.storageDirectory."+plugin.getStorageFromId(storageId)+".itemName"));
        String inventoryName = MenusOLD.getStorageMenuName(storageId);
        UUID teamUuid = team.getTeamUuid();
        Storage storage = Storage.getStorageFromCache(teamUuid, storageId);
        Inventory inventory;

        if (storage != null){
            inventory = storage.getInventoryInstance();

            if (inventory == null){
                inventory = Bukkit.createInventory(this, this.menuSlots, inventoryName);
                storage.setStorageInstance(inventory);

                byte[] contentBytes = storage.getStorageContent();
                ItemStack[] content = serializeManager.deserializeFromByte(contentBytes);
                inventory.setContents(content);
                // System.out.println("new content");
            }
            // System.out.println("Storage found in cache");
        }else{
            inventory = Bukkit.createInventory(this, this.menuSlots, inventoryName);
            
            if (team.hasStorage(storageId)){
                storage = team.getStorage(storageId);
                storage.setStorageInstance(inventory);
            }else{
                storage = new Storage(teamUuid, storageId, inventory, new byte[0], null);
            }

            byte[] contentBytes = storage.getStorageContent();
            ItemStack[] content = serializeManager.deserializeFromByte(contentBytes);
            inventory.setContents(content);
        }

        // System.out.println(serverName);
        // System.out.println(storage.getOpenedServerName());
        storage.setStorageContent(serializeManager.serializeToByte(inventory.getContents()));
        storage.setOpenedServerName(serverName);
        storage.saveStorageToCache(viewer.getPlayer(), true);
        storage.saveStorageToDatabase();

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

            inventory.setItem(slot, ItemBuilderOLD.getItem(material, buttonName, displayName, lore, headTexture, false));
        }

        return inventory;
    }


    @Override
    public void openMenu(int storageId){
        this.open(createInventory(storageId));
    }


    @Override()
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        UUID teamUuid = team.getTeamUuid();
        Integer storageId = getStorageId(clickEvent.getView().getTitle());
        
        if (clickEvent.getCurrentItem() != null){
            if (Buttons.PREVIOUS_STORAGE_BUTTON.isClickedButton(clickEvent)){
                storageId -= 1;
                Storage storage = Storage.getStorageFromCache(teamUuid, storageId);
                if (storage != null){
                    if (storage.getOpenedServerName() != null && !storage.getOpenedServerName().equals(serverName)){
                        player.sendMessage(messageManager.getMessage("common.storage_already_open"));
                        return;
                    }
                }
                clickEvent.setCancelled(true);
                new StorageMenu(player, previousMenu).openMenu(storageId);
            }else if (Buttons.NEXT_STORAGE_BUTTON.isClickedButton(clickEvent)){
                storageId += 1;
                Storage storage = Storage.getStorageFromCache(teamUuid, storageId);
                if (storage != null){
                    if (storage.getOpenedServerName() != null && !storage.getOpenedServerName().equals(serverName)){
                        player.sendMessage(messageManager.getMessage("common.storage_already_open"));
                        return;
                    }
                }
                clickEvent.setCancelled(true);
                new StorageMenu(player, previousMenu).openMenu(storageId);
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
        
        Storage storage = Storage.getStorageFromCache(teamUuid, storageId);
        if (storage != null){
            if (storage.getOpenedServerName() != null && !storage.getOpenedServerName().equals(serverName)){
                clickEvent.setCancelled(true);
                viewer.closeInventory();
                return;
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
        Storage storage = Storage.getStorageFromCache(teamUuid, storageId);

        if (storage != null){
            storage.setStorageContent(serializeManager.serializeToByte(inventoryContent));
            storage.setStorageInstanceContent(inventoryContent);
            storage.saveStorageToCache(player, false);
        }else if (team.hasStorage(storageId)){
            storage = team.getStorage(storageId);
            storage.setStorageInstance(storageInventory);
            storage.setStorageContent(serializeManager.serializeToByte(inventoryContent));
        }else{
            storage = new Storage(teamUuid, storageId, storageInventory,
                    serializeManager.serializeToByte(inventoryContent), null);
        }

        if (storage.getOpenedServerName() == null || storage.getOpenedServerName().equals(serverName)){
            final String openedServerName;
            if (storageInventory.getViewers().size() > 1){
                openedServerName = serverName;
            }else{
                openedServerName = null;
            }

            // System.out.println("Viewers : " + storageInventory.getViewers().size());

            // System.out.println("CLOSE : " + storageInventory.getViewers().size());
            // System.out.println("CLOSE : " + openedServerName);
            
            storage.setOpenedServerName(openedServerName);
            storage.sendStorageAcrossServers(plugin, player);
            storage.saveStorageToDatabase();
            if (openedServerName == null){
                storage.removeStorageFromCache();
                // System.out.println("REMOVE");
            }
            // System.out.println("SAVE");
        }

        
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
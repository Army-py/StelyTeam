package fr.army.stelyteam.events.inventories;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.builder.ItemBuilder;


public class StorageDirectoryInventory extends TeamMenu {


    public StorageDirectoryInventory(Player viewer){
        super(viewer);
    }


    public Inventory createInventory(Team team) {
        Integer slots = config.getInt("inventoriesSlots.storageDirectory");
        Inventory inventory = Bukkit.createInventory(this, slots, config.getString("inventoriesName.storageDirectory"));
        Integer level = team.getTeamStorageLvl();

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
            Integer slot = config.getInt("inventories.storageDirectory."+str+".slot");
            String headTexture;
            Material material;
            String name;
            List<String> lore;

            if (level >= config.getInt("inventories.storageDirectory."+str+".level") || str.equals("close")){
                material = Material.getMaterial(config.getString("inventories.storageDirectory."+str+".itemType"));
                headTexture = config.getString("inventories.storageDirectory."+str+".headTexture");
                if (str.equals("close")){
                    name = config.getString("inventories.storageDirectory."+str+".itemName");
                }else{
                    name = config.getString(config.getString("inventories.storageDirectory."+str+".itemName"));
                }
                lore = config.getStringList("inventories.storageDirectory."+str+".lore");

                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
            }else{
                material = Material.getMaterial(config.getString("storageNotUnlock.itemType"));
                name = config.getString("storageNotUnlock.itemName");
                lore = config.getStringList("storageNotUnlock.lore");
                headTexture = config.getString("storageNotUnlock.headTexture");

                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
            }
        }
        return inventory;
    }


    public void openMenu(Team team){
        this.open(createInventory(team));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        Team team = plugin.getDatabaseManager().getTeamFromPlayerName(playerName);
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Material itemType = clickEvent.getCurrentItem().getType();

        // Fermeture ou retour en arrière de l'inventaire
        if (itemName.equals(config.getString("inventories.storageDirectory.close.itemName"))){
            // Inventory inventory = inventoryBuilder.createMemberInventory(playerName, team);
            // player.openInventory(inventory);
            new MemberInventory(player).openMenu(team);
        }else{
            for(String str : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
                String name = config.getString(config.getString("inventories.storageDirectory."+str+".itemName"));
                Material type = Material.getMaterial(config.getString("inventories.storageDirectory."+str+".itemType"));
                Integer storageId = config.getInt("inventories.storageDirectory."+str+".storageId");

                if (itemName.equals(name) && itemType.equals(type)){
                    // Inventory inventory = inventoryBuilder.createStorageInventory(teamId, storageId, name);
                    // Inventory inventory = inventoryBuilder.createStorageInventory(team, storageId);
                    // player.openInventory(inventory);
                    new StorageInventory(player).openMenu(team, storageId);
                    return;
                }
            }
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

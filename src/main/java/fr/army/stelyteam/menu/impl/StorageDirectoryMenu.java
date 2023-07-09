package fr.army.stelyteam.menu.impl;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.FixedMenu;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.utils.builder.ItemBuilder;


public class StorageDirectoryMenu extends FixedMenu {


    public StorageDirectoryMenu(Player viewer, TeamMenu previousMenu) {
        super(
            viewer,
            Menus.STORAGE_DIRECTORY_MENU.getName(),
            Menus.STORAGE_DIRECTORY_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);
        Integer level = team.getTeamStorageLvl();

        emptyCases(inventory, this.menuSlots, 0);

        for(String buttonName : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
            Integer slot = config.getInt("inventories.storageDirectory."+buttonName+".slot");
            String headTexture;
            Material material;
            String displayName;
            List<String> lore;

            if (level >= config.getInt("inventories.storageDirectory."+buttonName+".level") || buttonName.equals("close")){
                material = Material.getMaterial(config.getString("inventories.storageDirectory."+buttonName+".itemType"));
                headTexture = config.getString("inventories.storageDirectory."+buttonName+".headTexture");
                if (buttonName.equals("close")){
                    displayName = config.getString("inventories.storageDirectory."+buttonName+".itemName");
                }else{
                    displayName = config.getString(config.getString("inventories.storageDirectory."+buttonName+".itemName"));
                }
                lore = config.getStringList("inventories.storageDirectory."+buttonName+".lore");

                inventory.setItem(slot, ItemBuilder.getItem(material, buttonName, displayName, lore, headTexture, false));
            }else{
                material = Material.getMaterial(config.getString("storageNotUnlock.itemType"));
                displayName = config.getString("storageNotUnlock.itemName");
                lore = config.getStringList("storageNotUnlock.lore");
                headTexture = config.getString("storageNotUnlock.headTexture");

                inventory.setItem(slot, ItemBuilder.getItem(material, buttonName, displayName, lore, headTexture, false));
            }
        }
        return inventory;
    }


    @Override
    public void openMenu(){
        this.open(createInventory());
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Material itemType = clickEvent.getCurrentItem().getType();

        // Fermeture ou retour en arrière de l'inventaire
        if (Buttons.CLOSE_STORAGE_DIRECTORY_MENU_BUTTON.isClickedButton(clickEvent)){
            // new MemberMenu(player, this).openMenu();
            previousMenu.openMenu();
        }else{
            for(String str : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
                String name = config.getString(config.getString("inventories.storageDirectory."+str+".itemName"));
                Material type = Material.getMaterial(config.getString("inventories.storageDirectory."+str+".itemType"));
                Integer storageId = config.getInt("inventories.storageDirectory."+str+".storageId");

                if (itemName.equals(name) && itemType.equals(type)){
                    // new StorageMenu(player, this).openMenu(storageId);
                    player.sendMessage("§cCette fonctionnalité est temporairement désactivée.");
                    return;
                }
            }
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

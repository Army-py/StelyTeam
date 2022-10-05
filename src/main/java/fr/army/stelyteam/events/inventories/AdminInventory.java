package fr.army.stelyteam.events.inventories;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryBuilder;


public class AdminInventory {

    private InventoryClickEvent event;
    private YamlConfiguration config;
    private InventoryBuilder inventoryBuilder;


    public AdminInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.config = plugin.getConfig();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        // Ouverture des inventaires
        if (itemName.equals(config.getString("inventories.admin.manage.itemName"))){
            Inventory inventory = inventoryBuilder.createManageInventory(playerName);
            player.openInventory(inventory);
        }else if (itemName.equals(config.getString("inventories.admin.member.itemName"))){
            Inventory inventory = inventoryBuilder.createMemberInventory(playerName);
            player.openInventory(inventory);
            
        // Fermeture ou retour en arrière de l'inventaire
        }else if (itemName.equals(config.getString("inventories.admin.close.itemName"))){
            player.closeInventory();
        }
    }
}
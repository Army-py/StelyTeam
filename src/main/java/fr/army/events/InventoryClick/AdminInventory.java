package fr.army.events.InventoryClick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.App;
import fr.army.utils.InventoryGenerator;


public class AdminInventory {
    private InventoryClickEvent event;

    public AdminInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String inventoryName = event.getView().getTitle();

        // Ouverture des inventaires
        if (itemName.equals(App.config.getString("admin.manage.itemName"))){
            Inventory inventory = InventoryGenerator.createManageInventory(player.getName());
            player.openInventory(inventory);
        }else if (itemName.equals(App.config.getString("admin.member.itemName"))){
            Inventory inventory = InventoryGenerator.createMemberInventory();
            player.openInventory(inventory);
        }


        // Fermeture ou retour en arrière de l'inventaire
        if (App.sqlManager.isOwner(player.getName()) || App.sqlManager.isAdmin(player.getName())){
            if (itemName.equals(App.config.getString("admin.close.itemName")) && inventoryName.equals(App.config.getString("inventoriesName.admin"))){
                player.closeInventory();
            }
        }
    }
}

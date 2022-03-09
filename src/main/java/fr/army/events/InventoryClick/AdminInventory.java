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
        if(event.getCurrentItem() == null || !App.config.getConfigurationSection("inventoriesName").getValues(true).containsValue(event.getView().getTitle())){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        // Ouverture des inventaires
        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(App.config.getString("admin.manage.itemName"))){
            Inventory inventory = InventoryGenerator.createManageInventory(player.getName());
            player.openInventory(inventory);
        }else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(App.config.getString("admin.member.itemName"))){
            Inventory inventory = InventoryGenerator.createMemberInventory();
            player.openInventory(inventory);
        }


        // Fermeture ou retour en arrière de l'inventaire
        if (App.sqlManager.isOwner(player.getName()) || App.sqlManager.isAdmin(player.getName())){
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(App.config.getString("admin.close.itemName")) && event.getView().getTitle().equals(App.config.getString("inventoriesName.admin"))){
                player.closeInventory();
            }
        }
    }
}

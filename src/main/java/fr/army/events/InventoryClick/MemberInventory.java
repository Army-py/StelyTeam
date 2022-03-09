package fr.army.events.InventoryClick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.App;
import fr.army.utils.InventoryGenerator;


public class MemberInventory {
    private InventoryClickEvent event;

    public MemberInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String inventoryName = event.getView().getTitle();

        // Fermeture ou retour en arrière de l'inventaire
        if (App.sqlManager.isOwner(player.getName()) || App.sqlManager.isAdmin(player.getName())){
            if (itemName.equals(App.config.getString("member.close.itemName")) && inventoryName.equals(App.config.getString("inventoriesName.member"))){
                Inventory inventory = InventoryGenerator.createAdminInventory();
                player.openInventory(inventory);
            }
        }else if (App.sqlManager.isMember(player.getName())){
            if (itemName.equals(App.config.getString("member.close.itemName")) && inventoryName.equals(App.config.getString("inventoriesName.member"))){
                player.closeInventory();
            }
        }
    }
}

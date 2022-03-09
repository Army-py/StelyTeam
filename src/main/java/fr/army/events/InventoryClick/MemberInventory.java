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
        if(event.getCurrentItem() == null || !App.config.getConfigurationSection("inventoriesName").getValues(true).containsValue(event.getView().getTitle())){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        // Fermeture ou retour en arrière de l'inventaire
        if (App.sqlManager.isOwner(player.getName()) || App.sqlManager.isAdmin(player.getName())){
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(App.config.getString("member.close.itemName")) && event.getView().getTitle().equals(App.config.getString("inventoriesName.member"))){
                Inventory inventory = InventoryGenerator.createAdminInventory();
                player.openInventory(inventory);
            }
        }else if (App.sqlManager.isMember(player.getName())){
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(App.config.getString("member.close.itemName")) && event.getView().getTitle().equals(App.config.getString("inventoriesName.member"))){
                player.closeInventory();
            }
        }
    }
}

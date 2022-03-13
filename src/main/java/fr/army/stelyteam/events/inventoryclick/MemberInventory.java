package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryGenerator;


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
        if (StelyTeamPlugin.sqlManager.isOwner(player.getName()) || StelyTeamPlugin.sqlManager.isAdmin(player.getName())){
            if (itemName.equals(StelyTeamPlugin.config.getString("member.close.itemName")) && inventoryName.equals(StelyTeamPlugin.config.getString("inventoriesName.member"))){
                Inventory inventory = InventoryGenerator.createAdminInventory();
                player.openInventory(inventory);
            }
        }else if (StelyTeamPlugin.sqlManager.isMember(player.getName())){
            if (itemName.equals(StelyTeamPlugin.config.getString("member.close.itemName")) && inventoryName.equals(StelyTeamPlugin.config.getString("inventoriesName.member"))){
                player.closeInventory();
            }
        }
    }
}

package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryGenerator;


public class MembersInventory {
    private InventoryClickEvent event;

    public MembersInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();


        // Fermeture ou retour en arrière de l'inventaire
        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.teamMembers.close.itemName"))){
            Inventory inventory = InventoryGenerator.createMembersInventory(player.getName());
            player.openInventory(inventory);
        }
    }
}

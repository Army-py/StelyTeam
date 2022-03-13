package fr.army.stelyteam.events.InventoryClick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.App;
import fr.army.stelyteam.utils.InventoryGenerator;


public class CreateTeamInventory {
    private InventoryClickEvent event;

    public CreateTeamInventory(InventoryClickEvent event) {
        this.event = event;
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(App.config.getString("createTeam.itemName"))){
            Inventory confirmInventory = InventoryGenerator.createConfirmInventory();
            player.openInventory(confirmInventory);
            App.playersCreateTeam.add(player.getName());
        }
    }
}

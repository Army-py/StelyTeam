package fr.army.events.InventoryClick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.App;
import fr.army.utils.InventoryGenerator;


public class CreateTeamInventory {
    private InventoryClickEvent event;

    public CreateTeamInventory(InventoryClickEvent event) {
        this.event = event;
    }


    public void onInventoryClick(){
        if(event.getCurrentItem() == null || !App.config.getConfigurationSection("inventoriesName").getValues(true).containsValue(event.getView().getTitle())){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(App.config.getString("createTeam.itemName"))){
            Inventory confirmInventory = InventoryGenerator.createConfirmInventory();
            player.openInventory(confirmInventory);
            App.playersCreateTeam.add(player.getName());
        }
    }
}

package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.InventoryGenerator;


public class CreateTeamInventory {
    private InventoryClickEvent event;

    public CreateTeamInventory(InventoryClickEvent event) {
        this.event = event;
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.createTeam.itemName"))){
            if (new EconomyManager().checkMoneyPlayer(player, StelyTeamPlugin.config.getDouble("prices.createTeam"))){
                Inventory confirmInventory = InventoryGenerator.createConfirmInventory();
                player.openInventory(confirmInventory);
                StelyTeamPlugin.playersTempActions.put(playerName, "createTeam");
            }else{
                player.sendMessage("Vous n'avez pas assez d'argent");
            }
        }
    }
}

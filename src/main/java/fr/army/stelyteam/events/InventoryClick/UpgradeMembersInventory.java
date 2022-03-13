package fr.army.stelyteam.events.InventoryClick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.App;
import fr.army.stelyteam.utils.InventoryGenerator;


public class UpgradeMembersInventory {
    private InventoryClickEvent event;

    public UpgradeMembersInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        String teamID = App.sqlManager.getTeamIDFromPlayer(player.getName());
        Integer level = App.sqlManager.getTeamLevel(teamID);

        
        // Gestion des items
        if (!itemName.equals(App.config.getString("upgradeTotalMembers.close.itemName"))){
            for(String str : App.config.getConfigurationSection("upgradeTotalMembers").getKeys(false)){
                String name = App.config.getString("upgradeTotalMembers."+str+".itemName");
                
                if (itemName.equals(name) && level+1 == App.config.getInt("upgradeTotalMembers."+str+".level")){
                    App.sqlManager.incrementTeamLevel(teamID);
                    player.sendMessage("Vous avez atteint le niveau "+(level+1));

                    Inventory inventory = InventoryGenerator.createUpgradeMembersInventory(player.getName());
                    player.openInventory(inventory);
                    break;
                }
            }
        }else{
            // Retour en arrière de l'inventaire
            Inventory inventory = InventoryGenerator.createManageInventory(player.getName());
            player.openInventory(inventory);
        }
    }
}

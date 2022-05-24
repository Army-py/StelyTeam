package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryGenerator;


public class UpgradeMembersInventory {
    private InventoryClickEvent event;

    public UpgradeMembersInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
        Integer level = StelyTeamPlugin.sqlManager.getTeamLevel(teamID);

        
        // Gestion des items
        if (!itemName.equals(StelyTeamPlugin.config.getString("inventories.upgradeTotalMembers.close.itemName"))){
            for(String str : StelyTeamPlugin.config.getConfigurationSection("inventories.upgradeTotalMembers").getKeys(false)){
                String name = StelyTeamPlugin.config.getString("inventories.upgradeTotalMembers."+str+".itemName");
                
                if (itemName.equals(name) && level+1 == StelyTeamPlugin.config.getInt("inventories.upgradeTotalMembers."+str+".level")){
                    StelyTeamPlugin.playersTempActions.put(playerName, "upgradeMembers");
                    Inventory inventory = InventoryGenerator.createConfirmInventory();
                    player.openInventory(inventory);
                    break;
                }else{
                    player.sendMessage("Vous devez débloquer le niveau précédent pour pouvoir acheter cette amélioration");
                    break;
                }
            }
        }else{
            // Retour en arrière de l'inventaire
            Inventory inventory = InventoryGenerator.createManageInventory(playerName);
            player.openInventory(inventory);
        }
    }
}

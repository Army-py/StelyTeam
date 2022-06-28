package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.InventoryGenerator;
import fr.army.stelyteam.utils.MessageManager;


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
                    if (new EconomyManager().checkMoneyPlayer(player, StelyTeamPlugin.config.getDouble("prices.upgradeLevel"+(level+1)))){
                        StelyTeamPlugin.playersTempActions.put(playerName, "upgradeMembers");
                        Inventory inventory = InventoryGenerator.createConfirmInventory();
                        player.openInventory(inventory);
                        return;
                    }else{
                        // player.sendMessage("Vous n'avez pas assez d'argent");
                        player.sendMessage(MessageManager.getMessage("common.not_enough_money"));
                        return;
                    }
                }else if (itemName.equals(name) && level >= StelyTeamPlugin.config.getInt("inventories.upgradeTotalMembers."+str+".level")){
                    // player.sendMessage("Vous avez déjà débloqué cette amélioration");
                    player.sendMessage(MessageManager.getMessage("manage_team.upgrade_levels.already_unlocked"));
                    return;
                }
            }
            // player.sendMessage("Vous devez débloquer le niveau précédent pour pouvoir acheter cette amélioration"); 
            player.sendMessage(MessageManager.getMessage("manage_team.upgrade_levels.must_unlock_previous_level"));
        }else{
            // Retour en arrière de l'inventaire
            Inventory inventory = InventoryGenerator.createManageInventory(playerName);
            player.openInventory(inventory);
        }
    }
}

package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.InventoryBuilder;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;
import fr.army.stelyteam.utils.SQLiteManager;


public class UpgradeMembersInventory {

    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private SQLManager sqlManager;
    private MessageManager messageManager;
    private EconomyManager economyManager;
    private InventoryBuilder inventoryBuilder;


    public UpgradeMembersInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        String teamID = sqlManager.getTeamIDFromPlayer(playerName);
        Integer level = sqlManager.getTeamLevel(teamID);

        
        // Gestion des items
        if (!itemName.equals(config.getString("inventories.upgradeTotalMembers.close.itemName"))){
            for(String str : config.getConfigurationSection("inventories.upgradeTotalMembers").getKeys(false)){
                String name = config.getString("inventories.upgradeTotalMembers."+str+".itemName");
                
                if (itemName.equals(name) && level+1 == config.getInt("inventories.upgradeTotalMembers."+str+".level")){
                    if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.upgrade.teamPlaces.level"+(level+1)))){
                        plugin.playersTempActions.put(playerName, "upgradeMembers");
                        Inventory inventory = inventoryBuilder.createConfirmInventory();
                        player.openInventory(inventory);
                        return;
                    }else{
                        // player.sendMessage("Vous n'avez pas assez d'argent");
                        player.sendMessage(messageManager.getMessage("common.not_enough_money"));
                        return;
                    }
                }else if (itemName.equals(name) && level >= config.getInt("inventories.upgradeTotalMembers."+str+".level")){
                    // player.sendMessage("Vous avez déjà débloqué cette amélioration");
                    player.sendMessage(messageManager.getMessage("manage_team.upgrade_levels.already_unlocked"));
                    return;
                }
            }
            // player.sendMessage("Vous devez débloquer le niveau précédent pour pouvoir acheter cette amélioration"); 
            player.sendMessage(messageManager.getMessage("manage_team.upgrade_levels.must_unlock_previous_level"));
        }else{
            // Retour en arrière de l'inventaire
            Inventory inventory = inventoryBuilder.createManageInventory(playerName);
            player.openInventory(inventory);
        }
    }
}

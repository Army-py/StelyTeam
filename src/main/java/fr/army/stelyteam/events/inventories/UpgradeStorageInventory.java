package fr.army.stelyteam.events.inventories;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;


public class UpgradeStorageInventory {

    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private YamlConfiguration config;
    private MySQLManager sqlManager;
    private MessageManager messageManager;
    private EconomyManager economyManager;
    private InventoryBuilder inventoryBuilder;


    public UpgradeStorageInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
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
        Material material = event.getCurrentItem().getType();

        String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
        Integer level = sqlManager.getTeamStorageLvl(teamID);
        
        // Gestion des items
        if (itemName.equals(config.getString("inventories.upgradeStorageAmount.close.itemName"))){
            // Retour en arrière de l'inventaire
            Inventory inventory = inventoryBuilder.createManageInventory(playerName);
            player.openInventory(inventory);
        }else if (!material.name().equals(config.getString("emptyCase"))){
            for(String str : config.getConfigurationSection("inventories.upgradeStorageAmount").getKeys(false)){
                String name = config.getString("inventories.upgradeStorageAmount."+str+".itemName");
                
                if (itemName.equals(name) && level+1 == config.getInt("inventories.upgradeStorageAmount."+str+".level")){
                    if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.upgrade.teamStorages.level"+(level+1)))){
                        cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.IMPROV_LVL_STORAGE));
                        Inventory inventory = inventoryBuilder.createConfirmInventory();
                        player.openInventory(inventory);
                        return;
                    }else{
                        // player.sendMessage("Vous n'avez pas assez d'argent");
                        player.sendMessage(messageManager.getMessage("common.not_enough_money"));
                        return;
                    }
                }else if (itemName.equals(name) && level >= config.getInt("inventories.upgradeStorageAmount."+str+".level")){
                    // player.sendMessage("Vous avez déjà débloqué cette amélioration");
                    player.sendMessage(messageManager.getMessage("common.already_unlocked"));
                    return;
                }
            }
            // player.sendMessage("Vous devez débloquer le niveau précédent pour pouvoir acheter cette amélioration"); 
            player.sendMessage(messageManager.getMessage("manage_team.upgrade_storages.must_unlock_previous_level"));
        }
    }
}

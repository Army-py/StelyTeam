package fr.army.stelyteam.events.inventories;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;
import fr.army.stelyteam.utils.manager.SQLiteManager;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;


public class ManageInventory {
    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private YamlConfiguration config;
    private MySQLManager sqlManager;
    private SQLiteManager sqliteManager;
    private MessageManager messageManager;
    private EconomyManager economyManager;
    private InventoryBuilder inventoryBuilder;


    public ManageInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.sqliteManager = plugin.getSQLiteManager();
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        Material itemType = event.getCurrentItem().getType();
        List<String> lore = event.getCurrentItem().getItemMeta().getLore();


        if (itemType.equals(Material.getMaterial(config.getString("noPermission.itemType"))) && lore.equals(config.getStringList("noPermission.lore"))){
            return;
        }
        
        // Liaisin des items avec leur fonction
        if (itemName.equals(config.getString("inventories.manage.editMembers.itemName"))){
            Inventory inventory = inventoryBuilder.createEditMembersInventory(playerName);
            player.openInventory(inventory);


        }else if (itemName.equals(config.getString("inventories.manage.editAlliances.itemName"))){
            Inventory inventory = inventoryBuilder.createEditAlliancesInventory(playerName);
            player.openInventory(inventory);


        }else if (itemName.equals(config.getString("inventories.manage.setTeamHome.itemName"))){
            cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.CREATE_HOME));
            Inventory inventory = inventoryBuilder.createConfirmInventory();
            player.openInventory(inventory);


        }else if (itemName.equals(config.getString("inventories.manage.removeTeamHome.itemName"))){
            String teamName = sqlManager.getTeamNameFromPlayerName(playerName);
            if (!sqliteManager.isSet(teamName)){
                player.sendMessage(messageManager.getMessage("manage_team.team_home.not_set"));
            }else{
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.DELETE_HOME));
                Inventory inventory = inventoryBuilder.createConfirmInventory();
                player.openInventory(inventory);
            }


        }else if (itemName.equals(config.getString("inventories.manage.buyTeamBank.itemName"))){
            String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
            
            if (!sqlManager.hasUnlockedTeamBank(teamID)){
                if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.buyTeamBank"))){
                    cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.BUY_TEAM_BANK));

                    Inventory inventory = inventoryBuilder.createConfirmInventory();
                    player.openInventory(inventory);
                }else{
                    player.sendMessage(messageManager.getMessage("common.not_enough_money"));
                }
            }else{
                player.sendMessage(messageManager.getMessage("manage_team.team_bank.already_unlocked"));
            }


        }else if (itemName.equals(config.getString("inventories.manage.upgradeTotalMembers.itemName"))){
            Inventory inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName);
            player.openInventory(inventory);


        }else if (itemName.equals(config.getString("inventories.manage.upgradeStorageAmount.itemName"))){
            Inventory inventory = inventoryBuilder.createUpgradeStorageInventory(playerName);
            player.openInventory(inventory);


        }else if (itemName.equals(config.getString("inventories.manage.editName.itemName"))){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamId"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_NAME));
                Inventory inventory = inventoryBuilder.createConfirmInventory();
                player.openInventory(inventory);
            }else{
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (itemName.equals(config.getString("inventories.manage.editPrefix.itemName"))){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamPrefix"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_PREFIX));
                Inventory inventory = inventoryBuilder.createConfirmInventory();
                player.openInventory(inventory);
            }else{
                // player.sendMessage("Vous n'avez pas assez d'argent");
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (itemName.equals(config.getString("inventories.manage.editDescription.itemName"))){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamDescription"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_DESCRIPTION));
                Inventory inventory = inventoryBuilder.createConfirmInventory();
                player.openInventory(inventory);
            }else{
                // player.sendMessage("Vous n'avez pas assez d'argent");
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (itemName.equals(config.getString("inventories.manage.removeTeam.itemName"))){
            cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.DELETE_TEAM));
            Inventory inventory = inventoryBuilder.createConfirmInventory();
            player.openInventory(inventory);


        }else if (itemName.equals(config.getString("inventories.manage.editPermissions.itemName"))){
            Inventory inventory = inventoryBuilder.createPermissionsInventory(playerName);
            player.openInventory(inventory);


        // Fermeture ou retour en arrière de l'inventaire
        }else if (itemName.equals(config.getString("inventories.manage.close.itemName"))){
            Inventory inventory = inventoryBuilder.createAdminInventory();
            player.openInventory(inventory);
        }
    }
}

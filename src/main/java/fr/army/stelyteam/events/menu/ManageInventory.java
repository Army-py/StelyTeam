package fr.army.stelyteam.events.menu;

import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class ManageInventory extends TeamMenu {

    final DatabaseManager mySqlManager = plugin.getDatabaseManager();
    final SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    final CacheManager cacheManager = plugin.getCacheManager();
    final MessageManager messageManager = plugin.getMessageManager();
    final EconomyManager economyManager = plugin.getEconomyManager();

    public ManageInventory(Player viewer){
        super(viewer);
    }


    public Inventory createInventory(Team team, String playerName) {
        Integer slots = config.getInt("inventoriesSlots.manage");
        Inventory inventory = Bukkit.createInventory(this, slots, config.getString("inventoriesName.manage"));

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.manage").getKeys(false)){
            Integer slot = config.getInt("inventories.manage."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.manage."+str+".itemType"));
            String name = config.getString("inventories.manage."+str+".itemName");
            List<String> lore = config.getStringList("inventories.manage."+str+".lore");
            String headTexture = config.getString("inventories.manage."+str+".headTexture");
            
            ItemStack item;
            
            if (plugin.playerHasPermission(playerName, team, str)){ 
                if (str.equals("buyTeamBank")){
                    item = ItemBuilder.getItem(material, name, lore, headTexture, team.isUnlockedTeamBank());
                }else {
                    item = ItemBuilder.getItem(material, name, lore, headTexture, false);
                }
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(slot, item);
        }
        return inventory;
    }


    public void openMenu(Team team){
        this.open(createInventory(team, viewer.getName()));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Material itemType = clickEvent.getCurrentItem().getType();
        List<String> lore = clickEvent.getCurrentItem().getItemMeta().getLore();
        Team team;


        if (itemType.equals(Material.getMaterial(config.getString("noPermission.itemType"))) && lore.equals(config.getStringList("noPermission.lore"))){
            return;
        }

        team = mySqlManager.getTeamFromPlayerName(playerName);
        
        // Liaisin des items avec leur fonction
        if (itemName.equals(config.getString("inventories.manage.editMembers.itemName"))){
            new EditMembersInventory(player).openMenu(team);


        }else if (itemName.equals(config.getString("inventories.manage.editAlliances.itemName"))){
            new EditAlliancesInventory(player).openMenu(team);


        }else if (itemName.equals(config.getString("inventories.manage.setTeamHome.itemName"))){
            cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.CREATE_HOME, team));
            new ConfirmInventory(player).openMenu();


        }else if (itemName.equals(config.getString("inventories.manage.removeTeamHome.itemName"))){
            if (!sqliteManager.isSet(team.getTeamName())){
                player.sendMessage(messageManager.getMessage("manage_team.team_home.not_set"));
            }else{
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.DELETE_HOME, team));
                new ConfirmInventory(player).openMenu();
            }


        }else if (itemName.equals(config.getString("inventories.manage.buyTeamBank.itemName"))){            
            if (!team.isUnlockedTeamBank()){
                if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.buyTeamBank"))){
                    cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.BUY_TEAM_BANK, team));

                    new ConfirmInventory(player).openMenu();
                }else{
                    player.sendMessage(messageManager.getMessage("common.not_enough_money"));
                }
            }else{
                player.sendMessage(messageManager.getMessage("manage_team.team_bank.already_unlocked"));
            }


        }else if (itemName.equals(config.getString("inventories.manage.upgradeTotalMembers.itemName"))){
            new UpgradeMembersInventory(player).openMenu(team);


        }else if (itemName.equals(config.getString("inventories.manage.upgradeStorageAmount.itemName"))){
            new UpgradeStorageInventory(player).openMenu(team);


        }else if (itemName.equals(config.getString("inventories.manage.editName.itemName"))){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamId"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_NAME, team));
                new ConfirmInventory(player).openMenu();
            }else{
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (itemName.equals(config.getString("inventories.manage.editPrefix.itemName"))){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamPrefix"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_PREFIX, team));
                new ConfirmInventory(player).openMenu();
            }else{
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (itemName.equals(config.getString("inventories.manage.editDescription.itemName"))){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamDescription"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_DESCRIPTION, team));
                new ConfirmInventory(player).openMenu();
            }else{
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (itemName.equals(config.getString("inventories.manage.removeTeam.itemName"))){
            cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.DELETE_TEAM, team));
            new ConfirmInventory(player).openMenu();


        }else if (itemName.equals(config.getString("inventories.manage.editPermissions.itemName"))){
            new PermissionsInventory(player).openMenu(team);


        // Fermeture ou retour en arrière de l'inventaire
        }else if (itemName.equals(config.getString("inventories.manage.close.itemName"))){
            new AdminInventory(player).openMenu();
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

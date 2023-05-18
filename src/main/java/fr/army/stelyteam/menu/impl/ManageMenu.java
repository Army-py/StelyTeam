package fr.army.stelyteam.menu.impl;

import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.Buttons;
import fr.army.stelyteam.utils.Menus;
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


public class ManageMenu extends TeamMenu {

    final DatabaseManager mySqlManager = plugin.getDatabaseManager();
    final SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    final CacheManager cacheManager = plugin.getCacheManager();
    final MessageManager messageManager = plugin.getMessageManager();
    final EconomyManager economyManager = plugin.getEconomyManager();

    public ManageMenu(Player viewer){
        super(
            viewer,
            Menus.MANAGE_MENU.getName(),
            Menus.MANAGE_MENU.getSlots()
        );
    }


    public Inventory createInventory(Team team, String playerName) {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);

        for(String str : config.getConfigurationSection("inventories.manage").getKeys(false)){
            Integer slot = config.getInt("inventories.manage."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.manage."+str+".itemType"));
            String name = config.getString("inventories.manage."+str+".itemName");
            List<String> lore = config.getStringList("inventories.manage."+str+".lore");
            String headTexture = config.getString("inventories.manage."+str+".headTexture");
            
            ItemStack item;
            
            
            if (str.equals("editAlliances") 
                    && plugin.playerHasPermissionInSection(playerName, team, "editAlliances")) {
                item = ItemBuilder.getItem(material, name, lore, headTexture, false);
            }else if (str.equals("editMembers") 
                    && plugin.playerHasPermissionInSection(playerName, team, "editMembers")) {
                item = ItemBuilder.getItem(material, name, lore, headTexture, false);
            }else if (plugin.playerHasPermission(playerName, team, str)){ 
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
        Material itemType = clickEvent.getCurrentItem().getType();
        List<String> lore = clickEvent.getCurrentItem().getItemMeta().getLore();
        Team team;


        if (itemType.equals(Material.getMaterial(config.getString("noPermission.itemType"))) && lore.equals(config.getStringList("noPermission.lore"))){
            return;
        }

        team = mySqlManager.getTeamFromPlayerName(playerName);
        
        // Liaisin des items avec leur fonction
        if (Buttons.EDIT_MEMBERS_MENU_BUTTON.isClickedButton(clickEvent)){
            new EditMembersMenu(player).openMenu(team);


        }else if (Buttons.EDIT_ALLIANCES_MENU_BUTTON.isClickedButton(clickEvent)){
            new EditAlliancesMenu(player).openMenu(team);


        }else if (Buttons.SET_TEAM_HOME_BUTTON.isClickedButton(clickEvent)){
            cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.CREATE_HOME, team));
            new ConfirmMenu(player).openMenu();


        }else if (Buttons.REMOVE_TEAM_HOME_BUTTON.isClickedButton(clickEvent)){
            if (!sqliteManager.isSet(team.getTeamName())){
                player.sendMessage(messageManager.getMessage("manage_team.team_home.not_set"));
            }else{
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.DELETE_HOME, team));
                new ConfirmMenu(player).openMenu();
            }


        }else if (Buttons.BUY_TEAM_BANK_BUTTON.isClickedButton(clickEvent)){            
            if (!team.isUnlockedTeamBank()){
                if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.buyTeamBank"))){
                    cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.BUY_TEAM_BANK, team));

                    new ConfirmMenu(player).openMenu();
                }else{
                    player.sendMessage(messageManager.getMessage("common.not_enough_money"));
                }
            }else{
                player.sendMessage(messageManager.getMessage("manage_team.team_bank.already_unlocked"));
            }


        }else if (Buttons.UPGRADE_LVL_MEMBERS_MENU_BUTTON.isClickedButton(clickEvent)){
            new UpgradeMembersMenu(player).openMenu(team);


        }else if (Buttons.UPGRADE_LVL_STORAGE_MENU_BUTTON.isClickedButton(clickEvent)){
            new UpgradeStorageMenu(player).openMenu(team);


        }else if (Buttons.EDIT_TEAM_NAME_BUTTON.isClickedButton(clickEvent)){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamId"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_NAME, team));
                new ConfirmMenu(player).openMenu();
            }else{
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (Buttons.EDIT_TEAM_PREFIX_BUTTON.isClickedButton(clickEvent)){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamPrefix"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_PREFIX, team));
                new ConfirmMenu(player).openMenu();
            }else{
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (Buttons.EDIT_TEAM_DESCRIPTION_BUTTON.isClickedButton(clickEvent)){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamDescription"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_DESCRIPTION, team));
                new ConfirmMenu(player).openMenu();
            }else{
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (Buttons.REMOVE_TEAM_BUTTON.isClickedButton(clickEvent)){
            cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.DELETE_TEAM, team));
            new ConfirmMenu(player).openMenu();


        }else if (Buttons.EDIT_TEAM_PERMISSIONS_MENU_BUTTON.isClickedButton(clickEvent)){
            new PermissionsMenu(player).openMenu(team);


        // Fermeture ou retour en arrière de l'inventaire
        }else if (Buttons.CLOSE_MANAGE_MENU_BUTTON.isClickedButton(clickEvent)){
            new AdminMenu(player).openMenu();
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

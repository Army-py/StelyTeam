package fr.army.stelyteam.menu.impl.old;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.menu.FixedMenuOLD;
import fr.army.stelyteam.menu.MenusOLD;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilderOLD;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class ManageMenu extends FixedMenuOLD {

    private final SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    private final EconomyManager economyManager = plugin.getEconomyManager();

    public ManageMenu(Player viewer, TeamMenuOLD previousMenu){
        super(
            viewer,
            MenusOLD.MANAGE_MENU.getName(),
            MenusOLD.MANAGE_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory(String playerName) {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);

        for(String buttonName : config.getConfigurationSection("inventories.manage").getKeys(false)){
            Integer slot = config.getInt("inventories.manage."+buttonName+".slot");
            Material material = Material.getMaterial(config.getString("inventories.manage."+buttonName+".itemType"));
            String displayName = config.getString("inventories.manage."+buttonName+".itemName");
            List<String> lore = config.getStringList("inventories.manage."+buttonName+".lore");
            String headTexture = config.getString("inventories.manage."+buttonName+".headTexture");
            
            ItemStack item;
            
            
            if (buttonName.equals("editAlliances") 
                    && plugin.playerHasPermissionInSection(playerName, team, "editAlliances")) {
                item = ItemBuilderOLD.getItem(material, buttonName, displayName, lore, headTexture, false);
            }else if (buttonName.equals("editMembers") 
                    && plugin.playerHasPermissionInSection(playerName, team, "editMembers")) {
                item = ItemBuilderOLD.getItem(material, buttonName, displayName, lore, headTexture, false);
            }else if (plugin.playerHasPermission(playerName, team, buttonName)){ 
                if (buttonName.equals("buyTeamBank")){
                    item = ItemBuilderOLD.getItem(material, buttonName, displayName, lore, headTexture, team.isUnlockedTeamBank());
                }else if (buttonName.equals("buyTeamClaim")){
                    item = ItemBuilderOLD.getItem(material, buttonName, displayName, lore, headTexture, team.isUnlockedTeamClaim());
                }else {
                    item = ItemBuilderOLD.getItem(material, buttonName, displayName, lore, headTexture, false);
                }
            }else{
                item = ItemBuilderOLD.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")),
                    "noPermission",
                    displayName, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(slot, item);
        }
        return inventory;
    }


    @Override
    public void openMenu(){
        this.open(createInventory(viewer.getName()));
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

        team = Team.initFromPlayerName(playerName);
        
        // Liaisin des items avec leur fonction
        if (Buttons.EDIT_MEMBERS_MENU_BUTTON.isClickedButton(clickEvent)){
            new EditMembersMenu(player, this).openMenu();


        }else if (Buttons.EDIT_ALLIANCES_MENU_BUTTON.isClickedButton(clickEvent)){
            new EditAlliancesMenu(player, this).openMenu();


        }else if (Buttons.SET_TEAM_HOME_BUTTON.isClickedButton(clickEvent)){
            cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.CREATE_HOME, team));
            new ConfirmMenu(player, this).openMenu();


        }else if (Buttons.REMOVE_TEAM_HOME_BUTTON.isClickedButton(clickEvent)){
            if (!sqliteManager.isSet(team.getTeamUuid())){
                player.sendMessage(messageManager.getMessage("manage_team.team_home.not_set"));
            }else{
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.DELETE_HOME, team));
                new ConfirmMenu(player, this).openMenu();
            }


        }else if (Buttons.BUY_TEAM_BANK_BUTTON.isClickedButton(clickEvent)){            
            if (!team.isUnlockedTeamBank()){
                if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.buyTeamBank"))){
                    cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.BUY_TEAM_BANK, team));

                    new ConfirmMenu(player, this).openMenu();
                }else{
                    player.sendMessage(messageManager.getMessage("common.not_enough_money"));
                }
            }else{
                player.sendMessage(messageManager.getMessage("manage_team.team_bank.already_unlocked"));
            }

        }else if (Buttons.BUY_TEAM_CLAIM_BUTTON.isClickedButton(clickEvent)){
            if (!team.isUnlockedTeamClaim()){
                if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.buyTeamClaim"))){
                    cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.BUY_TEAM_CLAIM, team));

                    new ConfirmMenu(player, this).openMenu();
                }else{
                    player.sendMessage(messageManager.getMessage("common.not_enough_money"));
                }
            }else{
                player.sendMessage(messageManager.getMessage("manage_team.team_claim.already_unlocked"));
            }

        }else if (Buttons.UPGRADE_LVL_MEMBERS_MENU_BUTTON.isClickedButton(clickEvent)){
            new UpgradeMembersMenu(player, this).openMenu();


        }else if (Buttons.UPGRADE_LVL_STORAGE_MENU_BUTTON.isClickedButton(clickEvent)){
            new UpgradeStorageMenu(player, this).openMenu();


        }else if (Buttons.EDIT_TEAM_NAME_BUTTON.isClickedButton(clickEvent)){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamId"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_NAME, team));
                new ConfirmMenu(player, this).openMenu();
            }else{
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (Buttons.EDIT_TEAM_PREFIX_BUTTON.isClickedButton(clickEvent)){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamPrefix"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_PREFIX, team));
                new ConfirmMenu(player, this).openMenu();
            }else{
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (Buttons.EDIT_TEAM_DESCRIPTION_BUTTON.isClickedButton(clickEvent)){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.editTeamDescription"))){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_DESCRIPTION, team));
                new ConfirmMenu(player, this).openMenu();
            }else{
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }


        }else if (Buttons.REMOVE_TEAM_BUTTON.isClickedButton(clickEvent)){
            cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.DELETE_TEAM, team));
            new ConfirmMenu(player, this).openMenu();


        }else if (Buttons.EDIT_TEAM_PERMISSIONS_MENU_BUTTON.isClickedButton(clickEvent)){
            new PermissionsMenu(player, this).openMenu();


        // Fermeture ou retour en arrière de l'inventaire
        }else if (Buttons.CLOSE_MANAGE_MENU_BUTTON.isClickedButton(clickEvent)){
            // new AdminMenu(player, this).openMenu();
            previousMenu.openMenu();
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

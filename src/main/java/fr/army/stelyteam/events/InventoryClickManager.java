package fr.army.stelyteam.events;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.inventories.*;
import fr.army.stelyteam.utils.SQLiteManager;


public class InventoryClickManager implements Listener{    

    private StelyTeamPlugin plugin;
    private SQLiteManager sqliteManager;
    private YamlConfiguration config;

    public InventoryClickManager(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.sqliteManager = plugin.getSQLiteManager();
        this.config = plugin.getConfig();
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event){
        if(!config.getConfigurationSection("inventoriesName").getValues(true).containsValue(event.getView().getTitle())){
            return;
        }
        
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        if (!sqliteManager.isRegistered(player.getName())) sqliteManager.registerPlayer(player);
        
        
        if (event.getCurrentItem() != null){
            if (event.getView().getTitle().equals(config.getString("inventoriesName.admin"))){
                new AdminInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.confirmInventory"))){
                new ConfirmInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.createTeam"))){
                new CreateTeamInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.manage"))){
                new ManageInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.member"))){
                new MemberInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.upgradeTotalMembers"))){
                new UpgradeMembersInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.editMembers"))){
                new EditMembersInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.editAlliances"))){
                new EditAlliancesInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.teamMembers"))){
                new MembersInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.permissions"))){
                new PermissionsInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.storageDirectory"))){
                new StorageDirectoryInventory(event, plugin).onInventoryClick();
            }else if (event.getView().getTitle().equals(config.getString("inventoriesName.upgradeStorageAmount"))){
                new UpgradeStorageInventory(event, plugin).onInventoryClick();
            }
        }

        if (config.getConfigurationSection("inventoriesName.storages").getValues(true).containsValue(event.getView().getTitle())){
            new StorageInventory(event, plugin).onInventoryClick();
        }
    }
}

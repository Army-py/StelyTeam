package fr.army.stelyteam.events;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.inventoryclick.*;
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getCurrentItem() == null || !config.getConfigurationSection("inventoriesName").getValues(true).containsValue(event.getView().getTitle())){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        if (!sqliteManager.isRegistered(player.getName())) sqliteManager.registerPlayer(player);

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
        }else if (event.getView().getTitle().equals(config.getString("inventoriesName.teamMembers"))){
            new MembersInventory(event, plugin).onInventoryClick();
        }else if (event.getView().getTitle().equals(config.getString("inventoriesName.permissions"))){
            new PermissionsInventory(event, plugin).onInventoryClick();
        }
    }
}

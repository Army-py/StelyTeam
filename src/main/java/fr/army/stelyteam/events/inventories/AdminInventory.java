package fr.army.stelyteam.events.inventories;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamInventory;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;


public class AdminInventory {

    private InventoryClickEvent event;
    private YamlConfiguration config;
    private DatabaseManager sqlManager;
    private InventoryBuilder inventoryBuilder;

    public AdminInventory(StelyTeamPlugin plugin) {
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getDatabaseManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }

    public AdminInventory(StelyTeamPlugin plugin, InventoryClickEvent event) {
        this.event = event;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getDatabaseManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        // Ouverture des inventaires
        if (itemName.equals(config.getString("inventories.admin.manage.itemName"))){
            Inventory inventory = inventoryBuilder.createManageInventory(playerName, sqlManager.getTeamFromPlayerName(playerName));
            player.openInventory(inventory);
        }else if (itemName.equals(config.getString("inventories.admin.member.itemName"))){
            Inventory inventory = inventoryBuilder.createMemberInventory(playerName, sqlManager.getTeamFromPlayerName(playerName));
            player.openInventory(inventory);
            
        // Fermeture ou retour en arrière de l'inventaire
        }else if (itemName.equals(config.getString("inventories.admin.close.itemName"))){
            player.closeInventory();
        }
    }
}

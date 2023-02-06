package fr.army.stelyteam.events.inventories;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.MySQLManager;


public class AlliancesInventory {

    private InventoryClickEvent event;
    private YamlConfiguration config;
    private MySQLManager mysqlManager;
    private InventoryBuilder inventoryBuilder;

    public AlliancesInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.config = plugin.getConfig();
        this.mysqlManager = plugin.getSQLManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }

    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();


        // Fermeture ou retour en arrière de l'inventaire
        if (itemName.equals(config.getString("inventories.teamAlliances.close.itemName"))){
            Inventory inventory = inventoryBuilder.createMemberInventory(playerName, mysqlManager.getTeamFromPlayerName(playerName));
            player.openInventory(inventory);
        }
    }
}

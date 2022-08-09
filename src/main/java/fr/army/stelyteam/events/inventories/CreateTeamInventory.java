package fr.army.stelyteam.events.inventories;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.InventoryBuilder;
import fr.army.stelyteam.utils.MessageManager;


public class CreateTeamInventory {
    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private EconomyManager economyManager;
    private MessageManager messageManager;
    private InventoryBuilder inventoryBuilder;

    public CreateTeamInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.economyManager = plugin.getEconomyManager();
        this.messageManager = plugin.getMessageManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(config.getString("inventories.createTeam.itemName"))){
            if (economyManager.checkMoneyPlayer(player, config.getDouble("prices.createTeam"))){
                Inventory confirmInventory = inventoryBuilder.createConfirmInventory();
                player.openInventory(confirmInventory);
                plugin.playersTempActions.put(playerName, "createTeam");
            }else{
                // player.sendMessage("Vous n'avez pas assez d'argent");
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }
        }
    }
}

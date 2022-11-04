package fr.army.stelyteam.events.inventories;

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


public class CreateTeamInventory {
    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private YamlConfiguration config;
    private EconomyManager economyManager;
    private MessageManager messageManager;
    private InventoryBuilder inventoryBuilder;

    public CreateTeamInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
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
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.CREATE_TEAM));
            }else{
                // player.sendMessage("Vous n'avez pas assez d'argent");
                player.sendMessage(messageManager.getMessage("common.not_enough_money"));
            }
        }
    }
}

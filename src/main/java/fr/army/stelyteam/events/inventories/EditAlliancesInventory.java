package fr.army.stelyteam.events.inventories;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversations.ConvAddAlliance;
import fr.army.stelyteam.conversations.ConvRemoveAlliance;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.MySQLManager;


public class EditAlliancesInventory {
    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private MySQLManager mysqlManager;
    private ConversationBuilder conversationBuilder;
    private InventoryBuilder inventoryBuilder;


    public EditAlliancesInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.mysqlManager = plugin.getSQLManager();
        this.conversationBuilder = plugin.getConversationBuilder();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void onInventoryClick(){
        String itemName;
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        Team team = mysqlManager.getTeamFromPlayerName(playerName);


        // Fermeture ou retour en arrière de l'inventaire
        itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        if (itemName.equals(config.getString("inventories.editAlliances.close.itemName"))){
            Inventory inventory = inventoryBuilder.createManageInventory(playerName, team);
            player.openInventory(inventory);
            return;
        }else if (itemName.equals(config.getString("inventories.editAlliances.addAlliance.itemName"))){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvAddAlliance(plugin));
            return;
        }else if (itemName.equals(config.getString("inventories.editAlliances.removeAlliance.itemName"))){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvRemoveAlliance(plugin));
            return;
        }
    }
}

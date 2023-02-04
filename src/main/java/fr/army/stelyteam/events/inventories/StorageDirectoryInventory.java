package fr.army.stelyteam.events.inventories;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.MySQLManager;


public class StorageDirectoryInventory {

    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private MySQLManager sqlManager;
    private InventoryBuilder inventoryBuilder;


    public StorageDirectoryInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String teamId = sqlManager.getTeamNameFromPlayerName(playerName);
        Team team = sqlManager.getTeamFromPlayerName(playerName);
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        Material itemType = event.getCurrentItem().getType();

        // Fermeture ou retour en arrière de l'inventaire
        if (itemName.equals(config.getString("inventories.storageDirectory.close.itemName"))){
            Inventory inventory = inventoryBuilder.createMemberInventory(playerName);
            player.openInventory(inventory);
        }else{
            for(String str : config.getConfigurationSection("inventories.storageDirectory").getKeys(false)){
                String name = config.getString(config.getString("inventories.storageDirectory."+str+".itemName"));
                Material type = Material.getMaterial(config.getString("inventories.storageDirectory."+str+".itemType"));
                Integer storageId = config.getInt("inventories.storageDirectory."+str+".storageId");

                if (itemName.equals(name) && itemType.equals(type)){
                    // Inventory inventory = inventoryBuilder.createStorageInventory(teamId, storageId, name);
                    Inventory inventory = inventoryBuilder.createStorageInventory(team, storageId, name);
                    player.openInventory(inventory);
                    return;
                }
            }
        }
    }


    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(plugin);
        Conversation conv = cf.withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
        conv.begin();
        return;
    }
}

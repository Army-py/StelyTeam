package fr.army.stelyteam.events.inventoryclick;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryBuilder;
import fr.army.stelyteam.utils.SQLManager;


public class StorageDirectoryInventory {

    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private SQLManager sqlManager;
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
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        Material itemType = event.getCurrentItem().getType();
        List<String> lore = event.getCurrentItem().getItemMeta().getLore();

        if (itemType.equals(Material.getMaterial(config.getString("noPermission.itemType"))) && lore.equals(config.getStringList("noPermission.lore"))){
            return;
        }

        // Fermeture ou retour en arrière de l'inventaire
        if (sqlManager.isOwner(playerName) || sqlManager.getMemberRank(playerName) <= 3){
            if (itemName.equals(config.getString("inventories.member.close.itemName"))){
                Inventory inventory = inventoryBuilder.createMemberInventory(playerName);
                player.openInventory(inventory);
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

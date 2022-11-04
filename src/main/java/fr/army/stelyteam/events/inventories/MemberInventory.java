package fr.army.stelyteam.events.inventories;

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
import fr.army.stelyteam.conversations.ConvAddMoney;
import fr.army.stelyteam.conversations.ConvWithdrawMoney;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;


public class MemberInventory {

    private InventoryClickEvent event;
    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private YamlConfiguration config;
    private SQLManager sqlManager;
    private MessageManager messageManager;
    private ConversationBuilder conversationBuilder;
    private InventoryBuilder inventoryBuilder;


    public MemberInventory(InventoryClickEvent event, StelyTeamPlugin plugin) {
        this.event = event;
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.messageManager = plugin.getMessageManager();
        this.conversationBuilder = plugin.getConversationBuilder();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String teamId = sqlManager.getTeamNameFromPlayerName(playerName);
        Material itemType = event.getCurrentItem().getType();
        List<String> lore = event.getCurrentItem().getItemMeta().getLore();

        if (itemType.equals(Material.getMaterial(config.getString("noPermission.itemType"))) && lore.equals(config.getStringList("noPermission.lore"))){
            return;
        }

        // Fermeture ou retour en arrière de l'inventaire
        if (sqlManager.isOwner(playerName) || sqlManager.getMemberRank(playerName) <= 3){
            if (itemName.equals(config.getString("inventories.member.close.itemName"))){
                Inventory inventory = inventoryBuilder.createAdminInventory();
                player.openInventory(inventory);
            }
        }else{
            if (itemName.equals(config.getString("inventories.member.close.itemName"))){
                player.closeInventory();
            }
        }
        
        if (itemName.equals(config.getString("inventories.member.seeTeamMembers.itemName"))){
            Inventory inventory = inventoryBuilder.createMembersInventory(playerName);
            player.openInventory(inventory);
        
        }else if (itemName.equals(config.getString("inventories.member.seeTeamAlliances.itemName"))){
            Inventory inventory = inventoryBuilder.createAlliancesInventory(playerName);
            player.openInventory(inventory);
        
        }else if (itemName.equals(config.getString("inventories.member.addTeamMoney.itemName"))){
            if (!sqlManager.hasUnlockedTeamBank(teamId)) {
                // player.sendMessage("Le compte de la team n'a pas encore été débloqué");
                player.sendMessage(messageManager.getMessage("common.team_bank_not_unlock"));
            }else{
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvAddMoney(plugin));
            }
        }else if (itemName.equals(config.getString("inventories.member.withdrawTeamMoney.itemName"))){
            if (!sqlManager.hasUnlockedTeamBank(teamId)) {
                // player.sendMessage("Le compte de la team n'a pas encore été débloqué");
                player.sendMessage(messageManager.getMessage("common.team_bank_not_unlock"));
            }else{
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvWithdrawMoney(plugin));
            }
        }else if (itemName.equals(config.getString("inventories.member.leaveTeam.itemName"))){
            player.closeInventory();
            if (!sqlManager.isOwner(playerName)){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.EDIT_NAME));
                Inventory inventory = inventoryBuilder.createConfirmInventory();
                player.openInventory(inventory);
            }else {
                // player.sendMessage("Tu ne peux pas quitter la team car tu es le créateur");
                player.sendMessage(messageManager.getMessage("other.owner_cant_leave_team"));
            }
        }else if (itemName.equals(config.getString("inventories.member.storageDirectory.itemName"))){
            Inventory inventory = inventoryBuilder.createStorageDirectoryInventory(playerName);
            player.openInventory(inventory);
        }
    }

    
    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(plugin);
        Conversation conv = cf.withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
        conv.begin();
        return;
    }
}

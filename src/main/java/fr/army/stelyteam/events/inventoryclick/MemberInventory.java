package fr.army.stelyteam.events.inventoryclick;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversations.ConvAddMoney;
import fr.army.stelyteam.conversations.ConvWithdrawMoney;
import fr.army.stelyteam.utils.InventoryGenerator;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.conversation.ConversationBuilder;


public class MemberInventory {
    private InventoryClickEvent event;

    public MemberInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        Material itemType = event.getCurrentItem().getType();
        List<String> lore = event.getCurrentItem().getItemMeta().getLore();

        if (itemType.equals(Material.getMaterial(StelyTeamPlugin.config.getString("noPermission.itemType"))) && lore.equals(StelyTeamPlugin.config.getStringList("noPermission.lore"))){
            return;
        }

        // Fermeture ou retour en arrière de l'inventaire
        if (StelyTeamPlugin.sqlManager.isOwner(playerName) || StelyTeamPlugin.sqlManager.getMemberRank(playerName) <= 3){
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.close.itemName"))){
                Inventory inventory = InventoryGenerator.createAdminInventory();
                player.openInventory(inventory);
            }
        }else{
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.close.itemName"))){
                player.closeInventory();
            }
        }
        
        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.seeTeamMembers.itemName"))){
            Inventory inventory = InventoryGenerator.createMembersInventory(playerName);
            player.openInventory(inventory);
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.addTeamMoney.itemName"))){
            player.closeInventory();
            new ConversationBuilder().getNameInput(player, new ConvAddMoney());
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.withdrawTeamMoney.itemName"))){
            player.closeInventory();
            new ConversationBuilder().getNameInput(player, new ConvWithdrawMoney());
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.leaveTeam.itemName"))){
            player.closeInventory();
            if (!StelyTeamPlugin.sqlManager.isOwner(playerName)){
                StelyTeamPlugin.playersTempActions.put(playerName, "leaveTeam");
                Inventory inventory = InventoryGenerator.createConfirmInventory();
                player.openInventory(inventory);
            }else {
                // player.sendMessage("Tu ne peux pas quitter la team car tu es le créateur");
                player.sendMessage(MessageManager.getMessage("other.owner_cant_leave_team"));
            }
        }
    }

    
    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(StelyTeamPlugin.instance);
        Conversation conv = cf.withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
        conv.begin();
        return;
    }
}

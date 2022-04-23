package fr.army.stelyteam.events.inventoryclick;

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


public class MemberInventory {
    private InventoryClickEvent event;

    public MemberInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        // Fermeture ou retour en arrière de l'inventaire
        if (StelyTeamPlugin.sqlManager.isOwner(playerName) || StelyTeamPlugin.sqlManager.isAdmin(playerName)){
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.close.itemName"))){
                Inventory inventory = InventoryGenerator.createAdminInventory();
                player.openInventory(inventory);
            }
        }else if (StelyTeamPlugin.sqlManager.isMember(playerName)){
            if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.close.itemName"))){
                player.closeInventory();
            }
        }

        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.members.itemName"))){
            Inventory inventory = InventoryGenerator.createMembersInventory(playerName);
            player.openInventory(inventory);
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.addTeamMoney.itemName"))){
            player.closeInventory();
            getNameInput(player, new ConvAddMoney());
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.WithdrawTeamMoney.itemName"))){
            player.closeInventory();
            getNameInput(player, new ConvWithdrawMoney());
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.member.leaveTeam.itemName"))){
            player.closeInventory();
            if (!StelyTeamPlugin.sqlManager.isOwner(playerName)){
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                StelyTeamPlugin.sqlManager.removeMember(playerName, teamID);
                player.sendMessage("Tu as quitté la team " + teamID);
            }else {
                player.sendMessage("Tu ne peux pas quitter la team car tu es le créateur");
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

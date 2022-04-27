package fr.army.stelyteam.events.inventoryclick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversations.ConvAddMember;
import fr.army.stelyteam.conversations.ConvEditOwner;
import fr.army.stelyteam.conversations.ConvRemoveMember;
import fr.army.stelyteam.utils.InventoryGenerator;


public class EditMembersInventory {
    private InventoryClickEvent event;

    public EditMembersInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        String itemName;
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String teamId = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);

        // Fermeture ou retour en arrière de l'inventaire
        itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.close.itemName"))){
            Inventory inventory = InventoryGenerator.createManageInventory(playerName);
            player.openInventory(inventory);
            return;
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.addMember.itemName"))){
            player.closeInventory();
            getNameInput(player, new ConvAddMember());
            return;
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.removeMember.itemName"))){
            player.closeInventory();
            getNameInput(player, new ConvRemoveMember());
            return;
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.editOwner.itemName"))){
            player.closeInventory();
            getNameInput(player, new ConvEditOwner());
            return;
        }

        itemName = removeFirstColors(event.getCurrentItem().getItemMeta().getDisplayName());
        if (StelyTeamPlugin.sqlManager.getMembers(teamId).contains(itemName) && StelyTeamPlugin.sqlManager.isOwner(playerName)){

            // System.out.println(itemName);
            // System.out.println(!StelyTeamPlugin.sqlManager.isOwner(itemName));
            // System.out.println(StelyTeamPlugin.sqlManager.getMemberRank(itemName));

            if (event.getClick().isRightClick()){
                if (!StelyTeamPlugin.sqlManager.isOwner(itemName) && StelyTeamPlugin.sqlManager.getMemberRank(itemName) < StelyTeamPlugin.getLastRank()){
                    StelyTeamPlugin.sqlManager.demoteMember(teamId, itemName);
                }
            }
            if (event.getClick().isLeftClick()){
                if (!StelyTeamPlugin.sqlManager.isOwner(itemName) && StelyTeamPlugin.sqlManager.getMemberRank(itemName) != 1){
                    StelyTeamPlugin.sqlManager.promoteMember(teamId, itemName);
                }
            }
            Inventory inventory = InventoryGenerator.createEditMembersInventory(playerName);
            player.openInventory(inventory);
        }
    }


    private String removeFirstColors(String name){
        Pattern pattern = Pattern.compile("§.");
        Matcher matcher = pattern.matcher(name);
        int colors = 0;
        while (matcher.find()) {
            colors++;
        }
        return name.substring(name.length() - (name.length() - colors * pattern.pattern().length()));
    }


    public void getNameInput(Player player, Prompt prompt) {
        ConversationFactory cf = new ConversationFactory(StelyTeamPlugin.instance);
        Conversation conv = cf.withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
        conv.begin();
        return;
    }
}

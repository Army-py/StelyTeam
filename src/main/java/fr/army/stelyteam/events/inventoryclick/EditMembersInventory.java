package fr.army.stelyteam.events.inventoryclick;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
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
import fr.army.stelyteam.utils.ConversationBuilder;
import fr.army.stelyteam.utils.InventoryGenerator;


public class EditMembersInventory {
    private InventoryClickEvent event;

    public EditMembersInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        String itemName;
        Material itemType = event.getCurrentItem().getType();
        List<String> lore = event.getCurrentItem().getItemMeta().getLore();
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String teamId = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
        


        if (itemType.equals(Material.getMaterial(StelyTeamPlugin.config.getString("noPermission.itemType"))) && lore.equals(StelyTeamPlugin.config.getStringList("noPermission.lore"))){
            return;
        }


        // Fermeture ou retour en arrière de l'inventaire
        itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.close.itemName"))){
            Inventory inventory = InventoryGenerator.createManageInventory(playerName);
            player.openInventory(inventory);
            return;
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.addMember.itemName"))){
            player.closeInventory();
            new ConversationBuilder().getNameInput(player, new ConvAddMember());
            return;
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.removeMember.itemName"))){
            player.closeInventory();
            new ConversationBuilder().getNameInput(player, new ConvRemoveMember());
            return;
        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.editOwner.itemName"))){
            player.closeInventory();
            new ConversationBuilder().getNameInput(player, new ConvEditOwner());
            return;
        }

        itemName = removeFirstColors(event.getCurrentItem().getItemMeta().getDisplayName());
        if (StelyTeamPlugin.sqlManager.getMembers(teamId).contains(itemName)){
            Integer authorRank = StelyTeamPlugin.sqlManager.getMemberRank(playerName);
            Integer playerRank = StelyTeamPlugin.sqlManager.getMemberRank(itemName);
            if (event.getClick().isRightClick()){
                if (playerRank <= authorRank){
                    return;
                }
                if (!StelyTeamPlugin.sqlManager.isOwner(itemName) && playerRank < StelyTeamPlugin.getLastRank()){
                    StelyTeamPlugin.sqlManager.demoteMember(teamId, itemName);
                }
            }else if (event.getClick().isLeftClick()){
                if (playerRank-1 <= authorRank){
                    return;
                }
                if (!StelyTeamPlugin.sqlManager.isOwner(itemName) && playerRank != 1){
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
        cf.withFirstPrompt(prompt);
        cf.withLocalEcho(false);
        cf.withTimeout(60);

        Conversation conv = cf.buildConversation(player);
        conv.begin();
        return;
    }
}

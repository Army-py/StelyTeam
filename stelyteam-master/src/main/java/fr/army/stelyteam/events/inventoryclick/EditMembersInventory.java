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
import fr.army.stelyteam.conversations.ConvRemoveMember;
import fr.army.stelyteam.utils.InventoryGenerator;


public class EditMembersInventory {
    private InventoryClickEvent event;

    public EditMembersInventory(InventoryClickEvent event) {
        this.event = event;
    }

    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String itemName;
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(player.getName());

        // Fermeture ou retour en arrière de l'inventaire
        itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.close.itemName"))){
            Inventory inventory = InventoryGenerator.createManageInventory(player.getName());
            player.openInventory(inventory);
            return;
        }

        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.addMember.itemName"))){
            player.closeInventory();
            StelyTeamPlugin.teamsJoinTeam.add(StelyTeamPlugin.sqlManager.getTeamIDFromOwner(player.getName()));
            getNameInput(player, new ConvAddMember());

        }else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.editMembers.removeMember.itemName"))){
            player.closeInventory();
            StelyTeamPlugin.teamsKickTeam.add(StelyTeamPlugin.sqlManager.getTeamIDFromOwner(player.getName()));
            getNameInput(player, new ConvRemoveMember());
        }

        itemName = removeFirstColors(event.getCurrentItem().getItemMeta().getDisplayName());
        if (StelyTeamPlugin.sqlManager.getMembers(teamID).contains(itemName) && StelyTeamPlugin.sqlManager.isOwner(player.getName())){
            if (StelyTeamPlugin.sqlManager.isAdmin(itemName)){
                if (event.getClick().isRightClick()) StelyTeamPlugin.sqlManager.demoteToMember(teamID, itemName);
            }else if (StelyTeamPlugin.sqlManager.isMember(itemName)){
                if (event.getClick().isLeftClick()) StelyTeamPlugin.sqlManager.promoteToAdmin(teamID, itemName);
            }
            Inventory inventory = InventoryGenerator.createEditMembersInventory(player.getName());
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
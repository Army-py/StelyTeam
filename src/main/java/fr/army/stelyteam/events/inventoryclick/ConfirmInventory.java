package fr.army.stelyteam.events.inventoryclick;

import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversations.ConvEditTeamID;
import fr.army.stelyteam.conversations.ConvEditTeamPrefix;
import fr.army.stelyteam.conversations.ConvGetTeamId;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.InventoryGenerator;
import fr.army.stelyteam.utils.TeamMembersUtils;
import fr.army.stelyteam.utils.conversation.ConversationBuilder;

public class ConfirmInventory {
    private InventoryClickEvent event;

    public ConfirmInventory(InventoryClickEvent event) {
        this.event = event;
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();


        if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.confirm.itemName"))){
            if (StelyTeamPlugin.containTeamAction(playerName, "addMember")){
                String teamId = StelyTeamPlugin.getTeamActions(playerName)[2];
                String senderName = StelyTeamPlugin.getTeamActions(playerName)[0];
                StelyTeamPlugin.removeTeamTempAction(playerName);
                StelyTeamPlugin.sqlManager.insertMember(playerName, teamId);
                player.closeInventory();
                player.sendMessage("Vous avez rejoint la team " + teamId);
                TeamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
                TeamMembersUtils.teamBroadcast(teamId, senderName, senderName + " a ajouté " + playerName + " à la team");
            }else if (StelyTeamPlugin.containTeamAction(playerName, "removeMember")){
                String teamId = StelyTeamPlugin.getTeamActions(playerName)[2];
                String receiverName = StelyTeamPlugin.getTeamActions(playerName)[1];
                Player receiver = Bukkit.getPlayer(receiverName);
                StelyTeamPlugin.removeTeamTempAction(playerName);
                StelyTeamPlugin.sqlManager.removeMember(receiverName, teamId);
                player.closeInventory();
                player.sendMessage("Vous avez exclu " + receiverName + " de la team");
                if (receiver != null) receiver.sendMessage("Vous avez été exclu de la team " + teamId);
                TeamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
                TeamMembersUtils.teamBroadcast(teamId, playerName, playerName + " a exclu " + receiverName);
            }else if (StelyTeamPlugin.containTeamAction(playerName, "editOwner")){
                String teamId = StelyTeamPlugin.getTeamActions(playerName)[2];
                String senderName = StelyTeamPlugin.getTeamActions(playerName)[0];
                String receiverName = StelyTeamPlugin.getTeamActions(playerName)[1];
                Player receiver = Bukkit.getPlayer(receiverName);
                StelyTeamPlugin.removeTeamTempAction(playerName);
                StelyTeamPlugin.sqlManager.updateTeamOwner(teamId, receiverName, senderName);
                player.closeInventory();
                player.sendMessage("Vous avez promu " + receiverName + " créateur de la team");
                if (receiver != null) receiver.sendMessage("Vous avez été promu créateur de la team " + teamId);
                TeamMembersUtils.refreshTeamMembersInventory(teamId, playerName);


            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "createTeam")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                player.closeInventory();
                new ConversationBuilder().getNameInput(player, new ConvGetTeamId());
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "buyTeamBank")){
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                StelyTeamPlugin.removePlayerTempAction(playerName);
                StelyTeamPlugin.sqlManager.updateUnlockTeamBank(teamID);
                new EconomyManager().removeMoneyPlayer(player, StelyTeamPlugin.config.getInt("prices.buyTeamBank"));
                player.sendMessage("Tu as debloqué le compte de la team");

                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
                TeamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "editName")){
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                StelyTeamPlugin.removePlayerTempAction(playerName);
                player.closeInventory();
                new ConversationBuilder().getNameInput(player, new ConvEditTeamID());
                TeamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "editPrefix")){
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                StelyTeamPlugin.removePlayerTempAction(playerName);
                player.closeInventory();
                new ConversationBuilder().getNameInput(player, new ConvEditTeamPrefix());
                TeamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "deleteTeam")){
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                StelyTeamPlugin.removePlayerTempAction(playerName);
                StelyTeamPlugin.sqlManager.removeTeam(teamID);
                player.closeInventory();
                player.sendMessage("Tu as supprimé la team");
                TeamMembersUtils.closeTeamMembersInventory(teamID, playerName);
                TeamMembersUtils.teamBroadcast(teamID, playerName, "La team " + teamID + " a été supprimée");
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "upgradeMembers")){
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                Integer level = StelyTeamPlugin.sqlManager.getTeamLevel(teamID);
                StelyTeamPlugin.removePlayerTempAction(playerName);
                StelyTeamPlugin.sqlManager.incrementTeamLevel(teamID);
                new EconomyManager().removeMoneyPlayer(player, StelyTeamPlugin.config.getInt("prices.upgradeLevel"+(level+1)));
                player.sendMessage("Vous avez atteint le niveau " + (level+1));

                Inventory inventory = InventoryGenerator.createUpgradeTotalMembersInventory(playerName);
                player.openInventory(inventory);
                TeamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "leaveTeam")){
                String teamId = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                StelyTeamPlugin.removePlayerTempAction(playerName);
                StelyTeamPlugin.sqlManager.removeMember(playerName, teamId);
                player.closeInventory();
                player.sendMessage("Tu as quitté la team " + teamId);
                TeamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
            }
        }

        else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.cancel.itemName"))){
            if (StelyTeamPlugin.containTeamAction(playerName, "addMember")){
                StelyTeamPlugin.removeTeamTempAction(playerName);
                player.closeInventory();
            }else if (StelyTeamPlugin.containTeamAction(playerName, "removeMember")){
                StelyTeamPlugin.removeTeamTempAction(playerName);
                player.closeInventory();
            }else if (StelyTeamPlugin.containTeamAction(playerName, "editOwner")){
                StelyTeamPlugin.removeTeamTempAction(playerName);
                player.closeInventory();

            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "createTeam")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createTeamInventory();
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "buyTeamBank")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "editName")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "editPrefix")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "deleteTeam")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "upgradeMembers")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createUpgradeTotalMembersInventory(playerName);
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerTempAction(playerName, "leaveTeam")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createMemberInventory(playerName);
                player.openInventory(inventory);
            }
        }
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

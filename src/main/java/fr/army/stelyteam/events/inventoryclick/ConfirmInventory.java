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
import fr.army.stelyteam.events.PlayerChat;
import fr.army.stelyteam.utils.InventoryGenerator;

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
                player.closeInventory();
                String teamId = StelyTeamPlugin.getTeamActions(playerName)[2];
                StelyTeamPlugin.removeTeamTempAction(playerName);
                StelyTeamPlugin.sqlManager.insertMember(playerName, teamId);
                player.sendMessage("Vous avez rejoint la team " + teamId);
            }else if (StelyTeamPlugin.containTeamAction(playerName, "removeMember")){
                player.closeInventory();
                String teamId = StelyTeamPlugin.getTeamActions(playerName)[2];
                String receiverName = StelyTeamPlugin.getTeamActions(playerName)[1];
                Player receiver = Bukkit.getPlayer(receiverName);
                StelyTeamPlugin.removeTeamTempAction(playerName);
                StelyTeamPlugin.sqlManager.removeMember(receiverName, teamId);
                player.sendMessage("Vous avez exclu " + receiverName + " de la team");
                if (receiver != null) receiver.sendMessage("Vous avez été exclu de la team " + teamId);
            }else if (StelyTeamPlugin.containTeamAction(playerName, "editOwner")){
                player.closeInventory();
                String teamId = StelyTeamPlugin.getTeamActions(playerName)[2];
                String senderName = StelyTeamPlugin.getTeamActions(playerName)[0];
                String receiverName = StelyTeamPlugin.getTeamActions(playerName)[1];
                Player receiver = Bukkit.getPlayer(receiverName);
                StelyTeamPlugin.removeTeamTempAction(playerName);
                StelyTeamPlugin.sqlManager.updateTeamOwner(teamId, receiverName, senderName);
                player.sendMessage("Vous avez promu " + receiverName + " créateur de la team");
                if (receiver != null) receiver.sendMessage("Vous avez été promu créateur de la team " + teamId);


            }else if (StelyTeamPlugin.containPlayerAction(playerName, "createTeam")){
                player.closeInventory();
                StelyTeamPlugin.removePlayerTempAction(playerName);
                player.sendMessage("Envoie un nom de team");
                StelyTeamPlugin.instance.getServer().getPluginManager().registerEvents(new PlayerChat(playerName), StelyTeamPlugin.instance);
            }else if (StelyTeamPlugin.containPlayerAction(playerName, "buyTeamBank")){
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                StelyTeamPlugin.removePlayerTempAction(playerName);
                StelyTeamPlugin.sqlManager.updateUnlockTeamBank(teamID);
                player.sendMessage("Tu as debloqué le compte de la team");

                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerAction(playerName, "editName")){
                player.closeInventory();
                StelyTeamPlugin.removePlayerTempAction(playerName);
                getNameInput(player, new ConvEditTeamID());
            }else if (StelyTeamPlugin.containPlayerAction(playerName, "editPrefix")){
                player.closeInventory();
                StelyTeamPlugin.removePlayerTempAction(playerName);
                getNameInput(player, new ConvEditTeamPrefix());
            }else if (StelyTeamPlugin.containPlayerAction(playerName, "deleteTeam")){
                player.closeInventory();
                String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(playerName);
                StelyTeamPlugin.removePlayerTempAction(playerName);
                StelyTeamPlugin.sqlManager.removeTeam(teamID, playerName);
                player.sendMessage("Tu as supprimé la team");
            }
        }

        else if (itemName.equals(StelyTeamPlugin.config.getString("inventories.confirmInventory.cancel.itemName"))){
            if (StelyTeamPlugin.containTeamAction(playerName, "addMember")){
                player.closeInventory();
                StelyTeamPlugin.removeTeamTempAction(playerName);
            }else if (StelyTeamPlugin.containTeamAction(playerName, "removeMember")){
                player.closeInventory();
                StelyTeamPlugin.removeTeamTempAction(playerName);
            }else if (StelyTeamPlugin.containTeamAction(playerName, "editOwner")){
                player.closeInventory();
                StelyTeamPlugin.removeTeamTempAction(playerName);

            }else if (StelyTeamPlugin.containPlayerAction(playerName, "createTeam")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createTeamInventory();
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerAction(playerName, "buyTeamBank")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerAction(playerName, "editName")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerAction(playerName, "editPrefix")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (StelyTeamPlugin.containPlayerAction(playerName, "deleteTeam")){
                StelyTeamPlugin.removePlayerTempAction(playerName);
                Inventory inventory = InventoryGenerator.createManageInventory(playerName);
                player.openInventory(inventory);
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

package fr.army.stelyteam.events.inventories;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversations.ConvEditTeamID;
import fr.army.stelyteam.conversations.ConvEditTeamPrefix;
import fr.army.stelyteam.conversations.ConvGetTeamId;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.InventoryBuilder;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.SQLManager;
import fr.army.stelyteam.utils.SQLiteManager;
import fr.army.stelyteam.utils.TeamMembersUtils;
import fr.army.stelyteam.utils.conversation.ConversationBuilder;

public class ConfirmInventory {

    private InventoryClickEvent clickEvent;
    private InventoryCloseEvent closeEvent;
    private StelyTeamPlugin plugin;
    private YamlConfiguration config;
    private SQLManager sqlManager;
    private SQLiteManager sqliteManager;
    private MessageManager messageManager;
    private EconomyManager economyManager;
    private ConversationBuilder conversationBuilder;
    private InventoryBuilder inventoryBuilder;
    private TeamMembersUtils teamMembersUtils;


    public ConfirmInventory(InventoryClickEvent clickEvent, StelyTeamPlugin plugin) {
        this.clickEvent = clickEvent;
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getSQLManager();
        this.sqliteManager = plugin.getSQLiteManager();
        this.messageManager = plugin.getMessageManager();
        this.economyManager = plugin.getEconomyManager();
        this.conversationBuilder = plugin.getConversationBuilder();
        this.inventoryBuilder = plugin.getInventoryBuilder();
        this.teamMembersUtils = plugin.getTeamMembersUtils();
    }


    public ConfirmInventory(InventoryCloseEvent closeEvent, StelyTeamPlugin plugin) {
        this.closeEvent = closeEvent;
        this.plugin = plugin;
    }


    public void onInventoryClick(){
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(config.getString("inventories.confirmInventory.confirm.itemName"))){
            if (plugin.containTeamAction(playerName, "removeMember")){
                String teamId = plugin.getTeamActions(playerName)[2];
                String receiverName = plugin.getTeamActions(playerName)[1];
                Player receiver = Bukkit.getPlayer(receiverName);
                plugin.removeTeamTempAction(playerName);
                sqlManager.removeMember(receiverName, teamId);
                player.closeInventory();
                // player.sendMessage("Vous avez exclu " + receiverName + " de la team");
                player.sendMessage(messageManager.getReplaceMessage("sender.exclude_member", receiverName));
                // if (receiver != null) receiver.sendMessage("Vous avez été exclu de la team " + teamId);
                if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.exclude_from_team", teamId));
                teamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
                // teamMembersUtils.teamBroadcast(teamId, playerName, playerName + " a exclu " + receiverName);
                teamMembersUtils.teamBroadcast(teamId, playerName, messageManager.replaceAuthorAndReceiver("broadcasts.player_exclude_member", playerName, receiverName));
            }else if (plugin.containTeamAction(playerName, "removeAlliance")){
                String teamName = plugin.getTeamActions(playerName)[2];
                String allianceName = plugin.getTeamActions(playerName)[1];
                plugin.removeTeamTempAction(playerName);
                sqlManager.removeAlliance(teamName, allianceName);;
                player.closeInventory();
                // player.sendMessage("Vous avez exclu " + receiverName + " de la team");
                player.sendMessage(messageManager.getReplaceMessage("sender.remove_alliance", allianceName));
                for (String memberName: sqlManager.getTeamMembers(allianceName)){
                    Player member = Bukkit.getPlayer(memberName);
                    if (member != null){
                        member.sendMessage(messageManager.getReplaceMessage("receiver.remove_alliance", teamName));
                    }
                }
                teamMembersUtils.refreshTeamMembersInventory(teamName, playerName);
                teamMembersUtils.refreshTeamMembersInventory(allianceName, playerName);
                // teamMembersUtils.teamBroadcast(teamId, playerName, playerName + " a exclu " + receiverName);
                teamMembersUtils.teamBroadcast(teamName, playerName, messageManager.replaceAuthorAndReceiver("broadcasts.player_remove_alliance", playerName, allianceName));
            }else if (plugin.containTeamAction(playerName, "editOwner")){
                String teamId = plugin.getTeamActions(playerName)[2];
                String senderName = plugin.getTeamActions(playerName)[0];
                String receiverName = plugin.getTeamActions(playerName)[1];
                Player receiver = Bukkit.getPlayer(receiverName);
                plugin.removeTeamTempAction(playerName);
                sqlManager.updateTeamOwner(teamId, receiverName, senderName);
                player.closeInventory();
                // player.sendMessage("Vous avez promu " + receiverName + " créateur de la team");
                player.sendMessage(messageManager.getReplaceMessage("sender.promote_owner", receiverName));
                // if (receiver != null) receiver.sendMessage("Vous avez été promu gérant de la team " + teamId);
                if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.promote_owner", teamId));
                teamMembersUtils.refreshTeamMembersInventory(teamId, playerName);


            }else if (plugin.containPlayerTempAction(playerName, "createTeam")){
                plugin.removePlayerTempAction(playerName);
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvGetTeamId(plugin));
            }else if (plugin.containPlayerTempAction(playerName, "buyTeamBank")){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                plugin.removePlayerTempAction(playerName);
                sqlManager.updateUnlockedTeamBank(teamID);
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.buyTeamBank"));
                // player.sendMessage("Tu as debloqué le compte de la team");
                player.sendMessage(messageManager.getMessage("manage_team.team_bank.unlock"));

                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
                // teamMembersUtils.teamBroadcast(teamID, playerName, "Le compte de team a été débloqué");
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceAuthor("broadcasts.team_bank_unlocked", playerName));
            }else if (plugin.containPlayerTempAction(playerName, "editName")){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                plugin.removePlayerTempAction(playerName);
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvEditTeamID(plugin));
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
            }else if (plugin.containPlayerTempAction(playerName, "editPrefix")){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                plugin.removePlayerTempAction(playerName);
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvEditTeamPrefix(plugin));
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
            }else if (plugin.containPlayerTempAction(playerName, "deleteTeam")){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                plugin.removePlayerTempAction(playerName);
                // teamMembersUtils.teamBroadcast(teamID, playerName, "La team " + teamID + " a été supprimée");
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceTeamId("broadcasts.team_deleted", teamID));
                sqlManager.removeTeam(teamID);
                sqliteManager.removeHome(teamID);
                player.closeInventory();
                // player.sendMessage("Tu as supprimé la team");
                player.sendMessage(messageManager.getMessage("manage_team.team_delete.deleted"));
                teamMembersUtils.closeTeamMembersInventory(teamID, playerName);
            }else if (plugin.containPlayerTempAction(playerName, "upgradeMembers")){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                Integer level = sqlManager.getImprovLvlMembers(teamID);
                Integer newLevel = level + 1;
                plugin.removePlayerTempAction(playerName);
                sqlManager.incrementImprovLvlMembers(teamID);
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamPlaces.level"+newLevel));
                // player.sendMessage("Vous avez atteint le niveau " + (level+1));
                player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_member_amount.new_upgrade", newLevel.toString()));

                Inventory inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName);
                player.openInventory(inventory);
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
                // teamMembersUtils.teamBroadcast(teamID, playerName, "Amélioration " + (level+1) + " de la team débloquée");
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceTeamId("broadcasts.new_member_amount_upgrade", teamID));
            }else if (plugin.containPlayerTempAction(playerName, "upgradeStorages")){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                Integer level = sqlManager.getTeamStorageLvl(teamID);
                Integer newLevel = level + 1;
                plugin.removePlayerTempAction(playerName);
                sqlManager.incrementTeamStorageLvl(teamID);
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamStorages.level"+newLevel));
                // player.sendMessage("Vous avez atteint le niveau " + (level+1));
                player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_storages.new_upgrade", newLevel.toString()));

                Inventory inventory = inventoryBuilder.createUpgradeStorageInventory(playerName);
                player.openInventory(inventory);
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
                // teamMembersUtils.teamBroadcast(teamID, playerName, "Amélioration " + (level+1) + " de la team débloquée");
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceTeamId("broadcasts.new_storage_upgrade", teamID));
            }else if (plugin.containPlayerTempAction(playerName, "leaveTeam")){
                String teamId = sqlManager.getTeamNameFromPlayerName(playerName);
                plugin.removePlayerTempAction(playerName);
                sqlManager.removeMember(playerName, teamId);
                player.closeInventory();
                // player.sendMessage("Tu as quitté la team " + teamId);
                player.sendMessage(messageManager.getReplaceMessage("other.leave_team", teamId));
                teamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
            }
        }

        else if (itemName.equals(config.getString("inventories.confirmInventory.cancel.itemName"))){
            if (plugin.containTeamAction(playerName, "removeMember")){
                plugin.removeTeamTempAction(playerName);
                player.closeInventory();
            }else if (plugin.containTeamAction(playerName, "removeAlliance")){
                plugin.removeTeamTempAction(playerName);
                player.closeInventory();
            }else if (plugin.containTeamAction(playerName, "editOwner")){
                plugin.removeTeamTempAction(playerName);
                player.closeInventory();

            }else if (plugin.containPlayerTempAction(playerName, "createTeam")){
                plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createTeamInventory();
                player.openInventory(inventory);
            }else if (plugin.containPlayerTempAction(playerName, "buyTeamBank")){
                plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (plugin.containPlayerTempAction(playerName, "editName")){
                plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (plugin.containPlayerTempAction(playerName, "editPrefix")){
                plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (plugin.containPlayerTempAction(playerName, "deleteTeam")){
                plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (plugin.containPlayerTempAction(playerName, "upgradeMembers")){
                plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName);
                player.openInventory(inventory);
            }else if (plugin.containPlayerTempAction(playerName, "upgradeStorages")){
                plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createUpgradeStorageInventory(playerName);
                player.openInventory(inventory);
            }else if (plugin.containPlayerTempAction(playerName, "leaveTeam")){
                plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createMemberInventory(playerName);
                player.openInventory(inventory);
            }
        }
    }


    public void onInventoryClose(){
        Player player = (Player) closeEvent.getPlayer();
        String playerName = player.getName();

        if (plugin.getTeamActions(playerName) != null) {
            plugin.removeTeamTempAction(playerName);
        }
        if (plugin.getPlayerActions(playerName) != null) {
            plugin.removePlayerTempAction(playerName);
        }
    }
}

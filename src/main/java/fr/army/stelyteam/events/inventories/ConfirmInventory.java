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
import fr.army.stelyteam.utils.TeamMembersUtils;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;
import fr.army.stelyteam.utils.manager.SQLiteManager;

public class ConfirmInventory {

    private InventoryClickEvent clickEvent;
    private InventoryCloseEvent closeEvent;
    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
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
        this.cacheManager = plugin.getCacheManager();
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
        this.cacheManager = plugin.getCacheManager();
    }


    public void onInventoryClick(){
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(config.getString("inventories.confirmInventory.confirm.itemName"))){
            // if (plugin.containTeamAction(playerName, "removeMember")){
            if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.REMOVE_MEMBER)){
                // String teamId = plugin.getTeamActions(playerName)[2];
                // String receiverName = plugin.getTeamActions(playerName)[1];
                TemporaryAction tempAction = cacheManager.getTempAction(playerName);
                String teamName = tempAction.getTeam().getTeamName();
                String receiverName = tempAction.getReceiverName();
                Player receiver = Bukkit.getPlayer(receiverName);
                // plugin.removeTeamTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                sqlManager.removeMember(receiverName, teamName);
                player.closeInventory();
                // player.sendMessage("Vous avez exclu " + receiverName + " de la team");
                player.sendMessage(messageManager.getReplaceMessage("sender.exclude_member", receiverName));
                // if (receiver != null) receiver.sendMessage("Vous avez été exclu de la team " + teamId);
                if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.exclude_from_team", teamName));
                teamMembersUtils.refreshTeamMembersInventory(teamName, playerName);
                // teamMembersUtils.teamBroadcast(teamId, playerName, playerName + " a exclu " + receiverName);
                teamMembersUtils.teamBroadcast(teamName, playerName, messageManager.replaceAuthorAndReceiver("broadcasts.player_exclude_member", playerName, receiverName));
            
            // }else if (plugin.containTeamAction(playerName, "removeAlliance")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.REMOVE_ALLIANCE)){
                // String teamName = plugin.getTeamActions(playerName)[2];
                // String allianceName = plugin.getTeamActions(playerName)[1];
                TemporaryAction tempAction = cacheManager.getTempAction(playerName);
                String teamName = tempAction.getTeam().getTeamName();
                String allianceName = tempAction.getReceiverName();
                // plugin.removeTeamTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
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
            
            // }else if (plugin.containTeamAction(playerName, "editOwner")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_OWNER)){
                // String teamId = plugin.getTeamActions(playerName)[2];
                // String senderName = plugin.getTeamActions(playerName)[0];
                // String receiverName = plugin.getTeamActions(playerName)[1];
                TemporaryAction tempAction = cacheManager.getTempAction(playerName);
                String teamName = tempAction.getTeam().getTeamName();
                String senderName = tempAction.getSenderName();
                String receiverName = tempAction.getReceiverName();
                Player receiver = Bukkit.getPlayer(receiverName);
                // plugin.removeTeamTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                sqlManager.updateTeamOwner(teamName, receiverName, senderName);
                player.closeInventory();
                // player.sendMessage("Vous avez promu " + receiverName + " créateur de la team");
                player.sendMessage(messageManager.getReplaceMessage("sender.promote_owner", receiverName));
                // if (receiver != null) receiver.sendMessage("Vous avez été promu gérant de la team " + teamId);
                if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.promote_owner", teamName));
                teamMembersUtils.refreshTeamMembersInventory(teamName, playerName);


            // }else if (plugin.containPlayerTempAction(playerName, "createTeam")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.CREATE_TEAM)){
                // plugin.removePlayerTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvGetTeamId(plugin));

            // }else if (plugin.containPlayerTempAction(playerName, "buyTeamBank")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.BUY_TEAM_BANK)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                // plugin.removePlayerTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                sqlManager.updateUnlockedTeamBank(teamID);
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.buyTeamBank"));
                // player.sendMessage("Tu as debloqué le compte de la team");
                player.sendMessage(messageManager.getMessage("manage_team.team_bank.unlock"));

                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
                // teamMembersUtils.teamBroadcast(teamID, playerName, "Le compte de team a été débloqué");
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceAuthor("broadcasts.team_bank_unlocked", playerName));

            // }else if (plugin.containPlayerTempAction(playerName, "editName")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_NAME)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                // plugin.removePlayerTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvEditTeamID(plugin));
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);

            // }else if (plugin.containPlayerTempAction(playerName, "editPrefix")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_PREFIX)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                // plugin.removePlayerTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvEditTeamPrefix(plugin));
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);

            // }else if (plugin.containPlayerTempAction(playerName, "deleteTeam")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.DELETE_TEAM)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                // plugin.removePlayerTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                // teamMembersUtils.teamBroadcast(teamID, playerName, "La team " + teamID + " a été supprimée");
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceTeamId("broadcasts.team_deleted", teamID));
                sqlManager.removeTeam(teamID);
                sqliteManager.removeHome(teamID);
                player.closeInventory();
                player.sendMessage(messageManager.getMessage("manage_team.team_delete.deleted"));
                teamMembersUtils.closeTeamMembersInventory(teamID, playerName);

            // }else if (plugin.containPlayerTempAction(playerName, "upgradeMembers")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_MEMBERS)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                Integer level = sqlManager.getImprovLvlMembers(teamID);
                Integer newLevel = level + 1;
                // plugin.removePlayerTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                sqlManager.incrementImprovLvlMembers(teamID);
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamPlaces.level"+newLevel));
                // player.sendMessage("Vous avez atteint le niveau " + (level+1));
                player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_member_amount.new_upgrade", newLevel.toString()));

                Inventory inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName);
                player.openInventory(inventory);
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
                // teamMembersUtils.teamBroadcast(teamID, playerName, "Amélioration " + (level+1) + " de la team débloquée");
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceTeamId("broadcasts.new_member_amount_upgrade", teamID));

            // }else if (plugin.containPlayerTempAction(playerName, "upgradeStorages")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_STORAGE)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                Integer level = sqlManager.getTeamStorageLvl(teamID);
                Integer newLevel = level + 1;
                // plugin.removePlayerTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                sqlManager.incrementTeamStorageLvl(teamID);
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamStorages.level"+newLevel));
                // player.sendMessage("Vous avez atteint le niveau " + (level+1));
                player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_storages.new_upgrade", newLevel.toString()));

                Inventory inventory = inventoryBuilder.createUpgradeStorageInventory(playerName);
                player.openInventory(inventory);
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
                // teamMembersUtils.teamBroadcast(teamID, playerName, "Amélioration " + (level+1) + " de la team débloquée");
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceTeamId("broadcasts.new_storage_upgrade", teamID));

            // }else if (plugin.containPlayerTempAction(playerName, "leaveTeam")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.LEAVE_TEAM)){
                String teamId = sqlManager.getTeamNameFromPlayerName(playerName);
                // plugin.removePlayerTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                sqlManager.removeMember(playerName, teamId);
                player.closeInventory();
                // player.sendMessage("Tu as quitté la team " + teamId);
                player.sendMessage(messageManager.getReplaceMessage("other.leave_team", teamId));
                teamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
            }
        }

        else if (itemName.equals(config.getString("inventories.confirmInventory.cancel.itemName"))){
            // if (plugin.containTeamAction(playerName, "removeMember")){
            if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.REMOVE_MEMBER)){
                // plugin.removeTeamTempAction(playerName);
                cacheManager.removePlayerAction(playerName);
                player.closeInventory();
            // }else if (plugin.containTeamAction(playerName, "removeAlliance")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.REMOVE_ALLIANCE)){
                // plugin.removeTeamTempAction(playerName);
                player.closeInventory();
            // }else if (plugin.containTeamAction(playerName, "editOwner")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_OWNER)){
                // plugin.removeTeamTempAction(playerName);
                player.closeInventory();

            // }else if (plugin.containPlayerTempAction(playerName, "createTeam")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.CREATE_TEAM)){
                // plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createTeamInventory();
                player.openInventory(inventory);
            // }else if (plugin.containPlayerTempAction(playerName, "buyTeamBank")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.BUY_TEAM_BANK)){
                // plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            // }else if (plugin.containPlayerTempAction(playerName, "editName")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_NAME)){
                // plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            // }else if (plugin.containPlayerTempAction(playerName, "editPrefix")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_PREFIX)){
                // plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            // }else if (plugin.containPlayerTempAction(playerName, "deleteTeam")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.DELETE_TEAM)){
                // plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            // }else if (plugin.containPlayerTempAction(playerName, "upgradeMembers")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_MEMBERS)){
                // plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName);
                player.openInventory(inventory);
            // }else if (plugin.containPlayerTempAction(playerName, "upgradeStorages")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_STORAGE)){
                // plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createUpgradeStorageInventory(playerName);
                player.openInventory(inventory);
            // }else if (plugin.containPlayerTempAction(playerName, "leaveTeam")){
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.LEAVE_TEAM)){
                // plugin.removePlayerTempAction(playerName);
                Inventory inventory = inventoryBuilder.createMemberInventory(playerName);
                player.openInventory(inventory);
            }
            cacheManager.removePlayerAction(playerName);
        }
    }


    public void onInventoryClose(){
        Player player = (Player) closeEvent.getPlayer();
        String playerName = player.getName();

        // if (plugin.getTeamActions(playerName) != null) {
        //     plugin.removeTeamTempAction(playerName);
        // }
        // if (plugin.getPlayerActions(playerName) != null) {
        //     plugin.removePlayerTempAction(playerName);
        // }
        if (cacheManager.playerHasAction(playerName)) {
            cacheManager.removePlayerAction(playerName);
        }
    }
}

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
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
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
            if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.REMOVE_MEMBER)){
                TemporaryAction tempAction = cacheManager.getTempAction(playerName);
                String teamName = tempAction.getTeam().getTeamName();
                String receiverName = tempAction.getReceiverName();
                Player receiver = Bukkit.getPlayer(receiverName);
                sqlManager.removeMember(receiverName, teamName);
                player.closeInventory();
                player.sendMessage(messageManager.getReplaceMessage("sender.exclude_member", receiverName));
                if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.exclude_from_team", teamName));
                teamMembersUtils.refreshTeamMembersInventory(teamName, playerName);
                teamMembersUtils.teamBroadcast(teamName, playerName, messageManager.replaceAuthorAndReceiver("broadcasts.player_exclude_member", playerName, receiverName));
            
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.REMOVE_ALLIANCE)){
                TemporaryAction tempAction = cacheManager.getTempAction(playerName);
                String teamName = tempAction.getTeam().getTeamName();
                String allianceName = tempAction.getReceiverName();
                sqlManager.removeAlliance(teamName, allianceName);;
                player.closeInventory();
                player.sendMessage(messageManager.getReplaceMessage("sender.remove_alliance", allianceName));
                for (String memberName: sqlManager.getTeamMembers(allianceName)){
                    Player member = Bukkit.getPlayer(memberName);
                    if (member != null){
                        member.sendMessage(messageManager.getReplaceMessage("receiver.remove_alliance", teamName));
                    }
                }
                teamMembersUtils.refreshTeamMembersInventory(teamName, playerName);
                teamMembersUtils.refreshTeamMembersInventory(allianceName, playerName);
                teamMembersUtils.teamBroadcast(teamName, playerName, messageManager.replaceAuthorAndReceiver("broadcasts.player_remove_alliance", playerName, allianceName));
            
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_OWNER)){
                TemporaryAction tempAction = cacheManager.getTempAction(playerName);
                String teamName = tempAction.getTeam().getTeamName();
                String senderName = tempAction.getSenderName();
                String receiverName = tempAction.getReceiverName();
                Player receiver = Bukkit.getPlayer(receiverName);
                sqlManager.updateTeamOwner(teamName, receiverName, senderName);
                player.closeInventory();
                player.sendMessage(messageManager.getReplaceMessage("sender.promote_owner", receiverName));
                if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.promote_owner", teamName));
                teamMembersUtils.refreshTeamMembersInventory(teamName, playerName);
            
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.CREATE_HOME)){
                player.closeInventory();
                String teamName = sqlManager.getTeamNameFromPlayerName(playerName);
                String worldName = player.getWorld().getName();
                Double x = player.getLocation().getX();
                Double y = player.getLocation().getY();
                Double z = player.getLocation().getZ();
                Double yaw = (double) player.getLocation().getYaw();

                if (sqliteManager.isSet(teamName)){
                    sqliteManager.updateHome(teamName, worldName, x, y, z, yaw);
                    player.sendMessage(messageManager.getMessage("manage_team.team_home.redefine"));
                }else{
                    sqliteManager.addHome(teamName, worldName, x, y, z, yaw);
                    player.sendMessage(messageManager.getMessage("manage_team.team_home.created"));
                }

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.DELETE_HOME)){
                player.closeInventory();
                String teamName = sqlManager.getTeamNameFromPlayerName(playerName);

                sqliteManager.removeHome(teamName);
                player.sendMessage(messageManager.getMessage("manage_team.team_home.deleted"));


            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.CREATE_TEAM)){
                cacheManager.removePlayerAction(playerName);
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvGetTeamId(plugin));

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.BUY_TEAM_BANK)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                sqlManager.updateUnlockedTeamBank(teamID);
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.buyTeamBank"));
                player.sendMessage(messageManager.getMessage("manage_team.team_bank.unlock"));

                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceAuthor("broadcasts.team_bank_unlocked", playerName));

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_NAME)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvEditTeamID(plugin));
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_PREFIX)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvEditTeamPrefix(plugin));
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.DELETE_TEAM)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceTeamId("broadcasts.team_deleted", teamID));
                sqlManager.removeTeam(teamID);
                sqliteManager.removeHome(teamID);
                player.closeInventory();
                player.sendMessage(messageManager.getMessage("manage_team.team_delete.deleted"));
                teamMembersUtils.closeTeamMembersInventory(teamID, playerName);

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_MEMBERS)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                Integer level = sqlManager.getImprovLvlMembers(teamID);
                Integer newLevel = level + 1;
                sqlManager.incrementImprovLvlMembers(teamID);
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamPlaces.level"+newLevel));
                player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_member_amount.new_upgrade", newLevel.toString()));

                Inventory inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName);
                player.openInventory(inventory);
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceTeamId("broadcasts.new_member_amount_upgrade", teamID));

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_STORAGE)){
                String teamID = sqlManager.getTeamNameFromPlayerName(playerName);
                Integer level = sqlManager.getTeamStorageLvl(teamID);
                Integer newLevel = level + 1;
                sqlManager.incrementTeamStorageLvl(teamID);
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamStorages.level"+newLevel));
                player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_storages.new_upgrade", newLevel.toString()));

                Inventory inventory = inventoryBuilder.createUpgradeStorageInventory(playerName);
                player.openInventory(inventory);
                teamMembersUtils.refreshTeamMembersInventory(teamID, playerName);
                teamMembersUtils.teamBroadcast(teamID, playerName, messageManager.replaceTeamId("broadcasts.new_storage_upgrade", teamID));

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.LEAVE_TEAM)){
                String teamId = sqlManager.getTeamNameFromPlayerName(playerName);
                sqlManager.removeMember(playerName, teamId);
                player.closeInventory();
                player.sendMessage(messageManager.getReplaceMessage("other.leave_team", teamId));
                teamMembersUtils.refreshTeamMembersInventory(teamId, playerName);
            }
            cacheManager.removePlayerAction(playerName);
        }

        else if (itemName.equals(config.getString("inventories.confirmInventory.cancel.itemName"))){
            if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.REMOVE_MEMBER)){
                player.closeInventory();
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.REMOVE_ALLIANCE)){
                player.closeInventory();
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_OWNER)){
                player.closeInventory();
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.CREATE_HOME)){
                player.closeInventory();
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.DELETE_HOME)){
                player.closeInventory();

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.CREATE_TEAM)){
                Inventory inventory = inventoryBuilder.createTeamInventory();
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.BUY_TEAM_BANK)){
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_NAME)){
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_PREFIX)){
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.DELETE_TEAM)){
                Inventory inventory = inventoryBuilder.createManageInventory(playerName);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_MEMBERS)){
                Inventory inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_STORAGE)){
                Inventory inventory = inventoryBuilder.createUpgradeStorageInventory(playerName);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.LEAVE_TEAM)){
                Inventory inventory = inventoryBuilder.createMemberInventory(playerName);
                player.openInventory(inventory);
            }
            cacheManager.removePlayerAction(playerName);
        }
    }


    public void onInventoryClose(){
        Player player = (Player) closeEvent.getPlayer();
        String playerName = player.getName();

        if (cacheManager.playerHasAction(playerName)) {
            cacheManager.removePlayerAction(playerName);
        }
    }
}

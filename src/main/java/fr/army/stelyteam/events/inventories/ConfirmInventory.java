package fr.army.stelyteam.events.inventories;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversations.ConvEditTeamDesc;
import fr.army.stelyteam.conversations.ConvEditTeamName;
import fr.army.stelyteam.conversations.ConvEditTeamPrefix;
import fr.army.stelyteam.conversations.ConvGetTeamName;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;
import fr.army.stelyteam.utils.manager.SQLiteManager;

public class ConfirmInventory {

    private InventoryClickEvent clickEvent;
    private InventoryCloseEvent closeEvent;
    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private YamlConfiguration config;
    private MySQLManager sqlManager;
    private SQLiteManager sqliteManager;
    private MessageManager messageManager;
    private EconomyManager economyManager;
    private ConversationBuilder conversationBuilder;
    private InventoryBuilder inventoryBuilder;


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
    }


    public ConfirmInventory(InventoryCloseEvent closeEvent, StelyTeamPlugin plugin) {
        this.closeEvent = closeEvent;
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
    }


    public void onInventoryClick(){
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        TemporaryAction tempAction = cacheManager.getTempAction(playerName);
        Team team = tempAction.getTeam();

        if (itemName.equals(config.getString("inventories.confirmInventory.confirm.itemName"))){
            if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.REMOVE_MEMBER)){
                String receiverName = tempAction.getReceiverName();
                Player receiver = Bukkit.getPlayer(receiverName);
                team.removeMember(receiverName);
                player.closeInventory();
                player.sendMessage(messageManager.getReplaceMessage("sender.exclude_member", receiverName));
                if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.exclude_from_team", team.getTeamName()));
                team.refreshTeamMembersInventory(playerName);
                team.teamBroadcast(playerName, messageManager.replaceAuthorAndReceiver("broadcasts.player_exclude_member", playerName, receiverName));
            
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.REMOVE_ALLIANCE)){
                String teamName = team.getTeamName();
                Team alliance = sqlManager.getTeamFromTeamName(tempAction.getReceiverName());
                String allianceName = alliance.getTeamName();
                
                team.removeAlliance(allianceName);
                team.refreshTeamMembersInventory(playerName);
                team.teamBroadcast(playerName, messageManager.replaceAuthorAndReceiver("broadcasts.player_remove_alliance", playerName, allianceName));
                alliance.refreshTeamMembersInventory(playerName);
                alliance.teamBroadcast(playerName, messageManager.getReplaceMessage("receiver.remove_alliance", teamName));
                player.closeInventory();
                player.sendMessage(messageManager.getReplaceMessage("sender.remove_alliance", allianceName));
            
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_OWNER)){
                String receiverName = tempAction.getReceiverName();
                Player receiver = Bukkit.getPlayer(receiverName);
                team.updateTeamOwner(receiverName);
                player.closeInventory();
                player.sendMessage(messageManager.getReplaceMessage("sender.promote_owner", receiverName));
                if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.promote_owner", team.getTeamName()));
                team.refreshTeamMembersInventory(playerName);
            
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
                conversationBuilder.getNameInput(player, new ConvGetTeamName(plugin));

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.BUY_TEAM_BANK)){
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.buyTeamBank"));
                team.unlockedTeamBank();
                player.sendMessage(messageManager.getMessage("manage_team.team_bank.unlock"));

                Inventory inventory = inventoryBuilder.createManageInventory(playerName, team);
                player.openInventory(inventory);
                team.refreshTeamMembersInventory(playerName);
                team.teamBroadcast(playerName, messageManager.replaceAuthor("broadcasts.team_bank_unlocked", playerName));

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_NAME)){
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvEditTeamName(plugin));
                team.refreshTeamMembersInventory(playerName);

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_PREFIX)){
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvEditTeamPrefix(plugin));
                team.refreshTeamMembersInventory(playerName);
            
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_DESCRIPTION)){
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvEditTeamDesc(plugin));
                team.refreshTeamMembersInventory(playerName);

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.DELETE_TEAM)){
                team.teamBroadcast(playerName, messageManager.replaceTeamId("broadcasts.team_deleted", team.getTeamName()));
                team.removeTeam();
                player.closeInventory();
                player.sendMessage(messageManager.getMessage("manage_team.team_delete.deleted"));
                team.closeTeamMembersInventory(playerName);

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_MEMBERS)){
                Integer level = team.getImprovLvlMembers();
                Integer newLevel = level + 1;
                team.incrementImprovLvlMembers();
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamPlaces.level"+newLevel));
                player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_member_amount.new_upgrade", newLevel.toString()));

                Inventory inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName);
                player.openInventory(inventory);
                team.refreshTeamMembersInventory(playerName);
                team.teamBroadcast(playerName, messageManager.replaceTeamId("broadcasts.new_member_amount_upgrade", team.getTeamName()));

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_STORAGE)){
                Integer level = team.getTeamStorageLvl();
                Integer newLevel = level + 1;
                team.incrementTeamStorageLvl();
                economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamStorages.level"+newLevel));
                player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_storages.new_upgrade", newLevel.toString()));

                Inventory inventory = inventoryBuilder.createUpgradeStorageInventory(playerName);
                player.openInventory(inventory);
                team.refreshTeamMembersInventory(playerName);
                team.teamBroadcast(playerName, messageManager.replaceTeamId("broadcasts.new_storage_upgrade", team.getTeamName()));

            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.LEAVE_TEAM)){
                team.removeMember(playerName);
                player.closeInventory();
                player.sendMessage(messageManager.getReplaceMessage("other.leave_team", team.getTeamName()));
                team.refreshTeamMembersInventory(playerName);
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
                Inventory inventory = inventoryBuilder.createManageInventory(playerName, team);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_NAME)){
                Inventory inventory = inventoryBuilder.createManageInventory(playerName, team);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_PREFIX)){
                Inventory inventory = inventoryBuilder.createManageInventory(playerName, team);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.EDIT_DESCRIPTION)){
                Inventory inventory = inventoryBuilder.createManageInventory(playerName, team);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.DELETE_TEAM)){
                Inventory inventory = inventoryBuilder.createManageInventory(playerName, team);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_MEMBERS)){
                Inventory inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.IMPROV_LVL_STORAGE)){
                Inventory inventory = inventoryBuilder.createUpgradeStorageInventory(playerName);
                player.openInventory(inventory);
            }else if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.LEAVE_TEAM)){
                Inventory inventory = inventoryBuilder.createMemberInventory(playerName, team);
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

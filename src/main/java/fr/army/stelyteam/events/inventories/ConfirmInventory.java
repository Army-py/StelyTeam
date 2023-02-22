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
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

public class ConfirmInventory {

    private InventoryClickEvent clickEvent;
    private InventoryCloseEvent closeEvent;
    private StelyTeamPlugin plugin;
    private CacheManager cacheManager;
    private YamlConfiguration config;
    private DatabaseManager sqlManager;
    private SQLiteDataManager sqliteManager;
    private MessageManager messageManager;
    private EconomyManager economyManager;
    private ConversationBuilder conversationBuilder;
    private InventoryBuilder inventoryBuilder;


    public ConfirmInventory(InventoryClickEvent clickEvent, StelyTeamPlugin plugin) {
        this.clickEvent = clickEvent;
        this.plugin = plugin;
        this.cacheManager = plugin.getCacheManager();
        this.config = plugin.getConfig();
        this.sqlManager = plugin.getDatabaseManager();
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
        String teamName;

        if (itemName.equals(config.getString("inventories.confirmInventory.confirm.itemName"))){
            String receiverName;
            Player receiver;
            Inventory inventory;
            Integer level;
            Integer newLevel;
            switch (cacheManager.getPlayerActionName(playerName)) {
                case REMOVE_MEMBER:
                    receiverName = tempAction.getReceiverName();
                    receiver = Bukkit.getPlayer(receiverName);
                    team.removeMember(receiverName);
                    player.closeInventory();
                    player.sendMessage(messageManager.getReplaceMessage("sender.exclude_member", receiverName));
                    if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.exclude_from_team", team.getTeamName()));
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceAuthorAndReceiver("broadcasts.player_exclude_member", playerName, receiverName));
                    break;
                case REMOVE_ALLIANCE:
                    Team alliance = sqlManager.getTeamFromTeamName(tempAction.getReceiverName());
                    String allianceName = alliance.getTeamName();
                    teamName = team.getTeamName();
                    
                    team.removeAlliance(allianceName);
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceAuthorAndReceiver("broadcasts.player_remove_alliance", playerName, allianceName));
                    alliance.refreshTeamMembersInventory(playerName);
                    alliance.teamBroadcast(playerName, messageManager.getReplaceMessage("receiver.remove_alliance", teamName));
                    player.closeInventory();
                    player.sendMessage(messageManager.getReplaceMessage("sender.remove_alliance", allianceName));
                    break;
                case EDIT_OWNER:
                    receiverName = tempAction.getReceiverName();
                    receiver = Bukkit.getPlayer(receiverName);
                    team.updateTeamOwner(receiverName);
                    player.closeInventory();
                    player.sendMessage(messageManager.getReplaceMessage("sender.promote_owner", receiverName));
                    if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.promote_owner", team.getTeamName()));
                    team.refreshTeamMembersInventory(playerName);
                    break;
                case CREATE_HOME:
                    player.closeInventory();
                    String worldName = player.getWorld().getName();
                    Double x = player.getLocation().getX();
                    Double y = player.getLocation().getY();
                    Double z = player.getLocation().getZ();
                    Double yaw = (double) player.getLocation().getYaw();
                    teamName = team.getTeamName();

                    if (sqliteManager.isSet(teamName)){
                        sqliteManager.updateHome(teamName, worldName, x, y, z, yaw);
                        player.sendMessage(messageManager.getMessage("manage_team.team_home.redefine"));
                    }else{
                        sqliteManager.addHome(teamName, worldName, x, y, z, yaw);
                        player.sendMessage(messageManager.getMessage("manage_team.team_home.created"));
                    }
                    break;
                case DELETE_HOME:
                    teamName = team.getTeamName();
                    player.closeInventory();
                    sqliteManager.removeHome(teamName);
                    player.sendMessage(messageManager.getMessage("manage_team.team_home.deleted"));
                    break;
                case CREATE_TEAM:
                    cacheManager.removePlayerAction(playerName);
                    player.closeInventory();
                    conversationBuilder.getNameInput(player, new ConvGetTeamName(plugin));
                    break;
                case BUY_TEAM_BANK:
                    economyManager.removeMoneyPlayer(player, config.getDouble("prices.buyTeamBank"));
                    team.unlockedTeamBank();
                    player.sendMessage(messageManager.getMessage("manage_team.team_bank.unlock"));

                    inventory = inventoryBuilder.createManageInventory(playerName, team);
                    player.openInventory(inventory);
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceAuthor("broadcasts.team_bank_unlocked", playerName));
                    break;
                case EDIT_NAME:
                    player.closeInventory();
                    conversationBuilder.getNameInput(player, new ConvEditTeamName(plugin));
                    team.refreshTeamMembersInventory(playerName);
                    break;
                case EDIT_PREFIX:
                    player.closeInventory();
                    conversationBuilder.getNameInput(player, new ConvEditTeamPrefix(plugin));
                    team.refreshTeamMembersInventory(playerName);
                    break;
                case EDIT_DESCRIPTION:
                    player.closeInventory();
                    conversationBuilder.getNameInput(player, new ConvEditTeamDesc(plugin));
                    team.refreshTeamMembersInventory(playerName);
                    break;
                case DELETE_TEAM:
                    team.teamBroadcast(playerName, messageManager.replaceTeamId("broadcasts.team_deleted", team.getTeamName()));
                    team.removeTeam();
                    player.closeInventory();
                    player.sendMessage(messageManager.getMessage("manage_team.team_delete.deleted"));
                    team.closeTeamMembersInventory(playerName);
                    break;
                case IMPROV_LVL_MEMBERS:
                    level = team.getImprovLvlMembers();
                    newLevel = level + 1;
                    team.incrementImprovLvlMembers();
                    economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamPlaces.level"+newLevel));
                    player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_member_amount.new_upgrade", newLevel.toString()));

                    inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName, team);
                    player.openInventory(inventory);
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceTeamId("broadcasts.new_member_amount_upgrade", team.getTeamName()));
                    break;
                case IMPROV_LVL_STORAGE:
                    level = team.getTeamStorageLvl();
                    newLevel = level + 1;
                    team.incrementTeamStorageLvl();
                    economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamStorages.level"+newLevel));
                    player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_storages.new_upgrade", newLevel.toString()));

                    inventory = inventoryBuilder.createUpgradeStorageInventory(playerName, team);
                    player.openInventory(inventory);
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceTeamId("broadcasts.new_storage_upgrade", team.getTeamName()));
                    break;
                case LEAVE_TEAM:
                    team.removeMember(playerName);
                    player.closeInventory();
                    player.sendMessage(messageManager.getReplaceMessage("other.leave_team", team.getTeamName()));
                    team.refreshTeamMembersInventory(playerName);
                    break;
                default:
                    break;
            }
            cacheManager.removePlayerAction(playerName);
        }

        else if (itemName.equals(config.getString("inventories.confirmInventory.cancel.itemName"))){
            Inventory inventory;
            switch (cacheManager.getPlayerActionName(playerName)) {
                case REMOVE_MEMBER:
                    player.closeInventory();
                    break;
                case REMOVE_ALLIANCE:
                    player.closeInventory();
                    break;
                case EDIT_OWNER:
                    player.closeInventory();
                    break;
                case CREATE_HOME:
                    inventory = inventoryBuilder.createManageInventory(playerName, team);
                    player.openInventory(inventory);
                    break;
                case DELETE_HOME:
                    inventory = inventoryBuilder.createManageInventory(playerName, team);
                    player.openInventory(inventory);
                    break;
                case CREATE_TEAM:
                    inventory = inventoryBuilder.createTeamInventory();
                    player.openInventory(inventory);
                    break;
                case BUY_TEAM_BANK:
                    inventory = inventoryBuilder.createManageInventory(playerName, team);
                    player.openInventory(inventory);
                    break;
                case EDIT_NAME:
                    inventory = inventoryBuilder.createManageInventory(playerName, team);
                    player.openInventory(inventory);
                    break;
                case EDIT_PREFIX:
                    inventory = inventoryBuilder.createManageInventory(playerName, team);
                    player.openInventory(inventory);
                    break;
                case EDIT_DESCRIPTION:
                    inventory = inventoryBuilder.createManageInventory(playerName, team);
                    player.openInventory(inventory);
                    break;
                case DELETE_TEAM:
                    inventory = inventoryBuilder.createManageInventory(playerName, team);
                    player.openInventory(inventory);
                    break;
                case IMPROV_LVL_MEMBERS:
                    inventory = inventoryBuilder.createUpgradeTotalMembersInventory(playerName, team);
                    player.openInventory(inventory);
                    break;
                case IMPROV_LVL_STORAGE:
                    inventory = inventoryBuilder.createUpgradeStorageInventory(playerName, team);
                    player.openInventory(inventory);
                    break;
                case LEAVE_TEAM:
                    inventory = inventoryBuilder.createMemberInventory(playerName, team);
                    player.openInventory(inventory);
                    break;
                default:
                    break;
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

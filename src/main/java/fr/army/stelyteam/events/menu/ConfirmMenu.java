package fr.army.stelyteam.events.menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.conversations.ConvEditTeamDesc;
import fr.army.stelyteam.conversations.ConvEditTeamName;
import fr.army.stelyteam.conversations.ConvEditTeamPrefix;
import fr.army.stelyteam.conversations.ConvGetTeamName;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class ConfirmMenu extends TeamMenu {

    final DatabaseManager mySqlManager = plugin.getDatabaseManager();
    final SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    final CacheManager cacheManager = plugin.getCacheManager();
    final MessageManager messageManager = plugin.getMessageManager();
    final EconomyManager economyManager = plugin.getEconomyManager();
    final ConversationBuilder conversationBuilder = plugin.getConversationBuilder();

    public ConfirmMenu(Player viewer){
        super(viewer);
    }


    public Inventory createInventory() {
        Integer slots = config.getInt("inventoriesSlots.confirmInventory");
        Inventory inventory = Bukkit.createInventory(this, slots, config.getString("inventoriesName.confirmInventory"));

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.confirmInventory").getKeys(false)){
            Material material = Material.getMaterial(config.getString("inventories.confirmInventory."+str+".itemType"));
            String name = config.getString("inventories.confirmInventory."+str+".itemName");
            List<String> lore = config.getStringList("inventories.confirmInventory."+str+".lore");
            String headTexture = config.getString("inventories.confirmInventory."+str+".headTexture");

            for(Integer slot : config.getIntegerList("inventories.confirmInventory."+str+".slots")){
                inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
            }
        }
        return inventory;
    }


    public void openMenu(){
        this.open(createInventory());
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        TemporaryAction tempAction = cacheManager.getTempAction(playerName);
        Team team = tempAction.getTeam();
        String teamName;

        if (itemName.equals(config.getString("inventories.confirmInventory.confirm.itemName"))){
            String receiverName;
            Player receiver;
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
                    Team alliance = mySqlManager.getTeamFromTeamName(tempAction.getReceiverName());
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

                    new ManageMenu(player).openMenu(team);
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

                    new UpgradeMembersMenu(player).openMenu(team);
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceTeamId("broadcasts.new_member_amount_upgrade", team.getTeamName()));
                    break;
                case IMPROV_LVL_STORAGE:
                    level = team.getTeamStorageLvl();
                    newLevel = level + 1;
                    team.incrementTeamStorageLvl();
                    economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamStorages.level"+newLevel));
                    player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_storages.new_upgrade", newLevel.toString()));

                    new UpgradeStorageMenu(player).openMenu(team);
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
                    new ManageMenu(player).openMenu(team);
                    break;
                case DELETE_HOME:
                    new ManageMenu(player).openMenu(team);
                    break;
                case CREATE_TEAM:
                    new CreateTeamMenu(player).openMenu();
                    break;
                case BUY_TEAM_BANK:
                    new ManageMenu(player).openMenu(team);
                    break;
                case EDIT_NAME:
                    new ManageMenu(player).openMenu(team);
                    break;
                case EDIT_PREFIX:
                    new ManageMenu(player).openMenu(team);
                    break;
                case EDIT_DESCRIPTION:
                    new ManageMenu(player).openMenu(team);
                    break;
                case DELETE_TEAM:
                    new ManageMenu(player).openMenu(team);
                    break;
                case IMPROV_LVL_MEMBERS:
                    new UpgradeMembersMenu(player).openMenu(team);
                    break;
                case IMPROV_LVL_STORAGE:
                    new UpgradeStorageMenu(player).openMenu(team);
                    break;
                case LEAVE_TEAM:
                    new MemberMenu(player).openMenu(team);
                    break;
                default:
                    break;
            }
            cacheManager.removePlayerAction(playerName);
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {
        Player player = (Player) closeEvent.getPlayer();
        String playerName = player.getName();

        if (cacheManager.playerHasAction(playerName)) {
            cacheManager.removePlayerAction(playerName);
        }
    }
}

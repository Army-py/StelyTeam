package fr.army.stelyteam.menu.impl.old;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.conversation.ConvEditTeamDesc;
import fr.army.stelyteam.conversation.ConvEditTeamName;
import fr.army.stelyteam.conversation.ConvEditTeamPrefix;
import fr.army.stelyteam.conversation.ConvGetTeamName;
import fr.army.stelyteam.menu.FixedMenuOLD;
import fr.army.stelyteam.menu.MenusOLD;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ItemBuilderOLD;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class ConfirmMenu extends FixedMenuOLD {

    private final SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    private final EconomyManager economyManager = plugin.getEconomyManager();
    private final ConversationBuilder conversationBuilder = plugin.getConversationBuilder();

    public ConfirmMenu(Player viewer, TeamMenuOLD previousMenu){
        super(
            viewer,
            MenusOLD.CONFIRM_MENU.getName(),
            MenusOLD.CONFIRM_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);

        for(String buttonName : config.getConfigurationSection("inventories.confirmInventory").getKeys(false)){
            Material material = Material.getMaterial(config.getString("inventories.confirmInventory."+buttonName+".itemType"));
            String displayName = config.getString("inventories.confirmInventory."+buttonName+".itemName");
            List<String> lore = config.getStringList("inventories.confirmInventory."+buttonName+".lore");
            String headTexture = config.getString("inventories.confirmInventory."+buttonName+".headTexture");

            for(Integer slot : config.getIntegerList("inventories.confirmInventory."+buttonName+".slots")){
                inventory.setItem(slot, ItemBuilderOLD.getItem(material, buttonName, displayName, lore, headTexture, false));
            }
        }
        return inventory;
    }


    @Override
    public void openMenu(){
        this.open(createInventory());
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        TemporaryAction tempAction = cacheManager.getTempAction(playerName);
        Team team = tempAction.getTeam();
        UUID teamUuid;
        String teamName;

        if (Buttons.CONFIRM_BUTTON.isClickedButton(clickEvent)){
            TemporaryActionNames actionName = cacheManager.getPlayerActionName(playerName);
            String receiverName;
            Player receiver;
            Integer level;
            Integer newLevel;
            switch (actionName) {
                case REMOVE_MEMBER:
                    receiverName = tempAction.getTargetName();
                    receiver = Bukkit.getPlayer(receiverName);
                    team.removeMember(receiverName);
                    player.closeInventory();
                    player.sendMessage(messageManager.getReplaceMessage("sender.exclude_member", receiverName));
                    if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.exclude_from_team", team.getTeamName()));
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceAuthorAndReceiver("broadcasts.player_exclude_member", playerName, receiverName));
                    break;
                case REMOVE_ALLIANCE:
                    Team alliance = Team.init(UUID.fromString(tempAction.getTargetName()));
                    String allianceName = alliance.getTeamName();
                    teamName = team.getTeamName();
                    
                    team.removeAlliance(alliance);
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceAuthorAndTeamName("broadcasts.player_remove_alliance", playerName, allianceName));
                    alliance.refreshTeamMembersInventory(playerName);
                    alliance.teamBroadcast(playerName, messageManager.getReplaceMessage("receiver.remove_alliance", teamName));
                    player.closeInventory();
                    player.sendMessage(messageManager.getReplaceMessage("sender.remove_alliance", allianceName));
                    break;
                case EDIT_OWNER:
                    receiverName = tempAction.getTargetName();
                    receiver = Bukkit.getPlayer(receiverName);
                    team.updateTeamOwner(receiverName);
                    player.closeInventory();
                    player.sendMessage(messageManager.getReplaceMessage("sender.promote_owner", receiverName));
                    if (receiver != null && receiver.getName().equals(receiverName)) receiver.sendMessage(messageManager.getReplaceMessage("receiver.promote_owner", team.getTeamName()));
                    team.refreshTeamMembersInventory(playerName);
                    break;
                case CREATE_HOME:
                    player.closeInventory();
                    teamUuid = team.getTeamUuid();
                    String worldName = player.getWorld().getName();
                    Double x = player.getLocation().getX();
                    Double y = player.getLocation().getY();
                    Double z = player.getLocation().getZ();
                    Double yaw = (double) player.getLocation().getYaw();

                    if (sqliteManager.isSet(teamUuid)){
                        sqliteManager.updateHome(teamUuid, worldName, x, y, z, yaw);
                        player.sendMessage(messageManager.getMessage("manage_team.team_home.redefine"));
                    }else{
                        sqliteManager.addHome(teamUuid, worldName, x, y, z, yaw);
                        player.sendMessage(messageManager.getMessage("manage_team.team_home.created"));
                    }
                    break;
                case DELETE_HOME:
                    teamUuid = team.getTeamUuid();
                    player.closeInventory();
                    sqliteManager.removeHome(teamUuid);
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

                    new ManageMenu(player, previousMenu).openMenu();
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceAuthor("broadcasts.team_bank_unlocked", playerName));
                    break;
                case BUY_TEAM_CLAIM:
                    economyManager.removeMoneyPlayer(player, config.getDouble("prices.buyTeamClaim"));
                    team.unlockedTeamClaim();
                    player.sendMessage(messageManager.getMessage("manage_team.team_claim.unlock"));

                    new ManageMenu(player, previousMenu).openMenu();
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceAuthor("broadcasts.team_claim_unlocked", playerName));
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

                    new UpgradeMembersMenu(player, previousMenu).openMenu();
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceTeamId("broadcasts.new_member_amount_upgrade", team.getTeamName()));
                    break;
                case IMPROV_LVL_STORAGE:
                    level = team.getTeamStorageLvl();
                    newLevel = level + 1;
                    team.incrementTeamStorageLvl();
                    economyManager.removeMoneyPlayer(player, config.getDouble("prices.upgrade.teamStorages.level"+newLevel));
                    player.sendMessage(messageManager.getReplaceMessage("manage_team.upgrade_storages.new_upgrade", newLevel.toString()));

                    new UpgradeStorageMenu(player, previousMenu).openMenu();
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceTeamId("broadcasts.new_storage_upgrade", team.getTeamName()));
                    break;
                case LEAVE_TEAM:
                    team.removeMember(playerName);
                    player.closeInventory();
                    team.refreshTeamMembersInventory(playerName);
                    team.teamBroadcast(playerName, messageManager.replaceAuthor("broadcasts.player_leave_team", playerName));
                    player.sendMessage(messageManager.getReplaceMessage("other.leave_team", team.getTeamName()));
                    break;
                default:
                    break;
            }
            cacheManager.removePlayerActionName(playerName, actionName);
            cacheManager.addTeam(team);
        }

        else if (Buttons.CANCEL_BUTTON.isClickedButton(clickEvent)){
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
                    // new ManageMenu(player, this).openMenu();
                    previousMenu.openMenu();
                    break;
                case DELETE_HOME:
                    // new ManageMenu(player, this).openMenu();
                    previousMenu.openMenu();
                    break;
                case CREATE_TEAM:
                    // new CreateTeamMenu(player, this).openMenu();
                    previousMenu.openMenu();
                    break;
                case BUY_TEAM_BANK:
                    // new ManageMenu(player, this).openMenu();
                    previousMenu.openMenu();
                    break;
                case EDIT_NAME:
                    // new ManageMenu(player, this).openMenu();
                    previousMenu.openMenu();
                    break;
                case EDIT_PREFIX:
                    // new ManageMenu(player, this).openMenu();
                    previousMenu.openMenu();
                    break;
                case EDIT_DESCRIPTION:
                    // new ManageMenu(player, this).openMenu();
                    previousMenu.openMenu();
                    break;
                case DELETE_TEAM:
                    // new ManageMenu(player, this).openMenu();
                    previousMenu.openMenu();
                    break;
                case IMPROV_LVL_MEMBERS:
                    // new UpgradeMembersMenu(player, this).openMenu();
                    previousMenu.openMenu();
                    break;
                case IMPROV_LVL_STORAGE:
                    // new UpgradeStorageMenu(player, this).openMenu();
                    previousMenu.openMenu();
                    break;
                case LEAVE_TEAM:
                    // new MemberMenu(player, this).openMenu();
                    previousMenu.openMenu();
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

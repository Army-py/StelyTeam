package fr.army.stelyteam.events.menu;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.conversations.ConvAddMoney;
import fr.army.stelyteam.conversations.ConvWithdrawMoney;
import fr.army.stelyteam.utils.Buttons;
import fr.army.stelyteam.utils.Menus;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;



public class MemberMenu extends TeamMenu {

    final DatabaseManager mySqlManager = plugin.getDatabaseManager();
    final CacheManager cacheManager = plugin.getCacheManager();
    final MessageManager messageManager = plugin.getMessageManager();
    final ConversationBuilder conversationBuilder = plugin.getConversationBuilder();
    final ColorsBuilder colorsBuilder = plugin.getColorsBuilder();

    public MemberMenu(Player viewer){
        super(
            viewer,
            Menus.MEMBER_MENU.getName(),
            Menus.MEMBER_MENU.getSlots()
        );
    }


    public Inventory createInventory(Team team, String playerName) {
        String teamName = team.getTeamName();
        String teamPrefix = team.getTeamPrefix();
        String teamOwner = team.getTeamOwnerName();
        Integer teamMembersLelvel = team.getImprovLvlMembers();
        Integer teamMembers = team.getTeamMembers().size();
        String membershipDate = team.getMembershipDate(playerName);
        Double teamMoney = team.getTeamMoney();
        String teamDescription = team.getTeamDescription();
        Integer maxMembers = config.getInt("teamMaxMembers");
        String memberRank = plugin.getRankFromId(team.getMemberRank(playerName));
        String memberRankName = config.getString("ranks." + memberRank + ".name");
        String rankColor = config.getString("ranks." + memberRank + ".color");
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);

        for(String str : config.getConfigurationSection("inventories.member").getKeys(false)){
            Integer slot = config.getInt("inventories.member."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.member."+str+".itemType"));
            String name = config.getString("inventories.member."+str+".itemName");
            List<String> lore = config.getStringList("inventories.member."+str+".lore");
            String headTexture = config.getString("inventories.member."+str+".headTexture");
            ItemStack item;

            if (name.equals(config.getString("inventories.member.seeTeamBank.itemName"))){
                lore = replaceInLore(lore, "%TEAM_MONEY%", DoubleToString(teamMoney));
                lore = replaceInLore(lore, "%MAX_MONEY%", DoubleToString(config.getDouble("teamMaxMoney")));
            }else if (name.equals(config.getString("inventories.member.teamInfos.itemName"))){
                lore = replaceInLore(lore, "%NAME%", teamName);
                lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(teamPrefix));
                lore = replaceInLore(lore, "%OWNER%", teamOwner);
                lore = replaceInLore(lore, "%RANK%", rankColor + memberRankName);
                lore = replaceInLore(lore, "%DATE%", membershipDate);
                lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers));
                lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
                lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(teamDescription));
            }

            if (plugin.playerHasPermission(playerName, team, str)){ 
                item = ItemBuilder.getItem(material, name, lore, headTexture, false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            if (name.equals(config.getString("inventories.member.seeTeamBank.itemName"))){
                if (!team.isUnlockedTeamBank()){
                    item = ItemBuilder.getItem(
                        Material.getMaterial(config.getString("teamBankNotUnlock.itemType")),
                        config.getString("teamBankNotUnlock.itemName"),
                        Collections.emptyList(),
                        config.getString("teamBankNotUnlock.headTexture"),
                        false
                    );
                }
            }

            inventory.setItem(slot,  item);
        }
        return inventory;
    }


    public void openMenu(Team team){
        this.open(createInventory(team, viewer.getName()));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        Team team = mySqlManager.getTeamFromPlayerName(playerName);
        Material itemType = clickEvent.getCurrentItem().getType();
        List<String> lore = clickEvent.getCurrentItem().getItemMeta().getLore();

        if (itemType.equals(Material.getMaterial(config.getString("noPermission.itemType"))) && lore.equals(config.getStringList("noPermission.lore"))){
            return;
        }

        // Fermeture ou retour en arrière de l'inventaire        
        if (Buttons.CLOSE_MEMBER_MENU_BUTTON.isClickedButton(clickEvent)){
            if (team.isTeamOwner(playerName) 
                    || plugin.playerHasPermissionInSection(playerName, team, "manage")
                    || plugin.playerHasPermissionInSection(playerName, team, "editMembers")
                    || plugin.playerHasPermissionInSection(playerName, team, "editAlliances")){
                new AdminMenu(player).openMenu();
            }else{
                player.closeInventory();
            }
        }else if (Buttons.TEAM_MEMBERS_BUTTON.isClickedButton(clickEvent)){
            new MembersMenu(player).openMenu(team);
        
        }else if (Buttons.TEAM_ALLIANCES_BUTTON.isClickedButton(clickEvent)){
            new AlliancesMenu(player).openMenu(team);
        
        }else if (Buttons.ADD_MONEY_TEAM_BANK_BUTTON.isClickedButton(clickEvent)){
            if (!team.isUnlockedTeamBank()) {
                player.sendMessage(messageManager.getMessage("common.team_bank_not_unlock"));
            }else{
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvAddMoney(plugin));
            }
        }else if (Buttons.WITHDRAW_MONEY_TEAM_BANK_BUTTON.isClickedButton(clickEvent)){
            if (!team.isUnlockedTeamBank()) {
                player.sendMessage(messageManager.getMessage("common.team_bank_not_unlock"));
            }else{
                player.closeInventory();
                conversationBuilder.getNameInput(player, new ConvWithdrawMoney(plugin));
            }
        }else if (Buttons.LEAVE_TEAM_BUTTON.isClickedButton(clickEvent)){
            player.closeInventory();
            if (!team.isTeamOwner(playerName)){
                cacheManager.addTempAction(new TemporaryAction(playerName, TemporaryActionNames.LEAVE_TEAM, team));
                new ConfirmMenu(player).openMenu();
            }else {
                player.sendMessage(messageManager.getMessage("other.owner_cant_leave_team"));
            }
        }else if (Buttons.STORAGE_DIRECTORY_BUTTON.isClickedButton(clickEvent)){
            new StorageDirectoryMenu(player).openMenu(team);
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

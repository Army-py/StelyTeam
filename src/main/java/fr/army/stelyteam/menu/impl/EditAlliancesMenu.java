package fr.army.stelyteam.menu.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.conversation.ConvAddAlliance;
import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.team.Alliance;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class EditAlliancesMenu extends TeamMenu {

    final DatabaseManager mySqlManager = plugin.getDatabaseManager();
    final SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    final ColorsBuilder colorsBuilder = plugin.getColorsBuilder();
    final ConversationBuilder conversationBuilder = plugin.getConversationBuilder();
    final CacheManager cacheManager = plugin.getCacheManager();
    final MessageManager messageManager = plugin.getMessageManager();


    public EditAlliancesMenu(Player viewer){
        super(
            viewer,
            Menus.EDIT_ALLIANCES_MENU.getName(),
            Menus.EDIT_ALLIANCES_MENU.getSlots()
        );
    }


    public Inventory createInventory(Team team, String playerName) {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);
        Integer maxMembers = config.getInt("teamMaxMembers");

        emptyCases(inventory, this.menuSlots, 0);
        Integer headSlot = 0;
        for(Alliance alliance : team.getTeamAlliances()){
            Team teamAlliance = Team.init(alliance.getTeamUuid());
            String allianceName = teamAlliance.getTeamName();
            String alliancePrefix = teamAlliance.getTeamPrefix();
            String allianceOwnerName = teamAlliance.getTeamOwnerName();
            String allianceDate = alliance.getAllianceDate();
            String allianceDescription = teamAlliance.getTeamDescription();
            Integer teamMembersLelvel = teamAlliance.getImprovLvlMembers();
            Integer teamMembers = teamAlliance.getTeamMembers().size();
            ArrayList<String> allianceMembers = teamAlliance.getMembersName();
            UUID playerUUID = sqliteManager.getUUID(allianceOwnerName);
            // String itemName = colorsBuilder.replaceColor(alliancePrefix);
            String itemName = " ";
            List<String> lore = config.getStringList("teamAllianceLore");
            OfflinePlayer allianceOwner;
            ItemStack item;


            if (playerUUID == null) allianceOwner = Bukkit.getOfflinePlayer(allianceOwnerName);
            else allianceOwner = Bukkit.getOfflinePlayer(playerUUID);

            
            lore = replaceInLore(lore, "%OWNER%", allianceOwnerName);
            lore = replaceInLore(lore, "%NAME%", allianceName);
            lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(alliancePrefix));
            lore = replaceInLore(lore, "%DATE%", allianceDate);
            lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers));
            lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
            lore = replaceInLore(lore, "%MEMBERS%", allianceMembers.isEmpty() ? messageManager.getMessageWithoutPrefix("common.no_members") : String.join(", ", allianceMembers));
            lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(allianceDescription));
            
            
            item = ItemBuilder.getPlayerHead(allianceOwner, itemName, lore);
            // if (plugin.playerHasPermission(playerName, team, "seeTeamAlliances")){ 
            //     item = ItemBuilder.getPlayerHead(allianceOwner, itemName, lore);
            // }else{
            //     item = ItemBuilder.getItem(
            //         Material.getMaterial(config.getString("noPermission.itemType")), 
            //         itemName, 
            //         config.getStringList("noPermission.lore"), 
            //         config.getString("noPermission.headTexture"),
            //         false
            //     );
            // }

            inventory.setItem(headSlot, item);
            headSlot ++;
        }

        for(String str : config.getConfigurationSection("inventories.editAlliances").getKeys(false)){
            Integer slot = config.getInt("inventories.editAlliances."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.editAlliances."+str+".itemType"));
            String name = config.getString("inventories.editAlliances."+str+".itemName");
            String headTexture = config.getString("inventories.editAlliances."+str+".headTexture");
            ItemStack item;

            if (plugin.playerHasPermission(playerName, team, str)){ 
                item = ItemBuilder.getItem(
                    material,
                    name,
                    config.getStringList("inventories.editAlliances."+str+".lore"),
                    headTexture,
                    false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    name, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }
            inventory.setItem(slot, item);
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
        Team team = Team.initFromPlayerName(playerName);

        if (Buttons.CLOSE_EDIT_ALLIANCES_MENU_BUTTON.isClickedButton(clickEvent)){
            new ManageMenu(player).openMenu(team);
            return;
        }else if (Buttons.ADD_ALLIANCE_BUTTON.isClickedButton(clickEvent)){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvAddAlliance(plugin));
            return;
        }else if (Buttons.REMOVE_ALLIANCE_BUTTON.isClickedButton(clickEvent)){
            cacheManager.addTempAction(
                new TemporaryAction(playerName, TemporaryActionNames.CLICK_REMOVE_ALLIANCE, team)
            );
            new AlliancesMenu(player, Menus.REMOVE_ALLIANCES_MENU.getName()).openMenu(team);
            return;
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

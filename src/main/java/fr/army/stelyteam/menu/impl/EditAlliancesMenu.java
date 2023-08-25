package fr.army.stelyteam.menu.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.conversation.ConvAddAlliance;
import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.FixedMenu;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.team.Alliance;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;


public class EditAlliancesMenu extends FixedMenu {

    private final DatabaseManager mySqlManager = plugin.getDatabaseManager();
    private final ConversationBuilder conversationBuilder = plugin.getConversationBuilder();


    public EditAlliancesMenu(Player viewer, TeamMenu previousMenu) {
        super(
            viewer,
            Menus.EDIT_ALLIANCES_MENU.getName(),
            Menus.EDIT_ALLIANCES_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory(String playerName) {
        final int maxMembersPerLine = config.getInt("maxMembersInLore");
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
            List<String> allianceMembers = teamAlliance.getMembersName();
            UUID playerUUID = mySqlManager.getUUID(allianceOwnerName);
            String itemName = " ";
            List<String> lore = config.getStringList("teamAllianceLore");
            // OfflinePlayer allianceOwner;
            ItemStack item;


            List<String> playerNames = new ArrayList<>();
            for(int i = 0; i < allianceMembers.size(); i++){
                if (i != 0 && i % maxMembersPerLine == 0) playerNames.add("%BACKTOLINE%");
                playerNames.add(allianceMembers.get(i));
            }

            
            lore = replaceInLore(lore, "%OWNER%", allianceOwnerName);
            lore = replaceInLore(lore, "%NAME%", allianceName);
            lore = replaceInLore(lore, "%PREFIX%", ColorsBuilder.replaceColor(alliancePrefix));
            lore = replaceInLore(lore, "%DATE%", allianceDate);
            lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(teamMembers));
            lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+teamMembersLelvel));
            lore = replaceInLore(lore, "%MEMBERS%", allianceMembers.isEmpty() ? messageManager.getMessageWithoutPrefix("common.no_members") : String.join(", ", playerNames));
            lore = replaceInLore(lore, "%DESCRIPTION%", ColorsBuilder.replaceColor(allianceDescription));
            
            
            item = ItemBuilder.getPlayerHead(playerUUID, itemName, lore);
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

        for(String buttonName : config.getConfigurationSection("inventories.editAlliances").getKeys(false)){
            Integer slot = config.getInt("inventories.editAlliances."+buttonName+".slot");
            Material material = Material.getMaterial(config.getString("inventories.editAlliances."+buttonName+".itemType"));
            String displayName = config.getString("inventories.editAlliances."+buttonName+".itemName");
            String headTexture = config.getString("inventories.editAlliances."+buttonName+".headTexture");
            ItemStack item;

            if (plugin.playerHasPermission(playerName, team, buttonName)){ 
                item = ItemBuilder.getItem(
                    material,
                    buttonName,
                    displayName,
                    config.getStringList("inventories.editAlliances."+buttonName+".lore"),
                    headTexture,
                    false);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")),
                    "noPermission",
                    displayName, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }
            inventory.setItem(slot, item);
        }
        return inventory;
    }


    @Override
    public void openMenu(){
        this.open(createInventory(viewer.getName()));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        Team team = Team.initFromPlayerName(playerName);

        if (Buttons.CLOSE_EDIT_ALLIANCES_MENU_BUTTON.isClickedButton(clickEvent)){
            // new ManageMenu(player, this).openMenu();
            previousMenu.openMenu();
            return;
        }else if (Buttons.ADD_ALLIANCE_BUTTON.isClickedButton(clickEvent)){
            player.closeInventory();
            conversationBuilder.getNameInput(player, new ConvAddAlliance(plugin));
            return;
        }else if (Buttons.REMOVE_ALLIANCE_BUTTON.isClickedButton(clickEvent)){
            cacheManager.addTempAction(
                new TemporaryAction(playerName, TemporaryActionNames.CLICK_REMOVE_ALLIANCE, team)
            );
            new AlliancesMenu(player, Menus.REMOVE_ALLIANCES_MENU.getName(), this).openMenu();
            return;
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

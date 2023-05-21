package fr.army.stelyteam.menu.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.team.Alliance;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.TemporaryAction;
import fr.army.stelyteam.utils.TemporaryActionNames;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class AlliancesMenu extends TeamMenu {

    DatabaseManager mySqlManager = plugin.getDatabaseManager();
    SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    ColorsBuilder colorsBuilder = plugin.getColorsBuilder();
    CacheManager cacheManager = plugin.getCacheManager();
    MessageManager messageManager = plugin.getMessageManager();

    public AlliancesMenu(Player viewer){
        super(
            viewer,
            Menus.TEAM_ALLIANCES_MENU.getName(),
            Menus.TEAM_ALLIANCES_MENU.getSlots()
        );
    }

    public AlliancesMenu(Player viewer, String menuName){
        super(
            viewer,
            menuName,
            Menus.TEAM_ALLIANCES_MENU.getSlots()
        );
    }


    public Inventory createInventory(Team team, String playerName) {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);
        Integer headSlot = 0;
        for(Alliance alliance : team.getTeamAlliances()){
            Team teamAlliance = Team.init(alliance.getTeamUuid());
            String allianceName = teamAlliance.getName();
            String alliancePrefix = teamAlliance.getPrefix();
            String allianceOwnerName = teamAlliance.getTeamOwnerName();
            String allianceDate = alliance.getAllianceDate();
            Integer teamMembersLelvel = teamAlliance.getImprovLvlMembers();
            Integer teamMembers = teamAlliance.getTeamMembers().size();
            Integer maxMembers = config.getInt("teamMaxMembers");
            String allianceDescription = teamAlliance.getDescription();
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
            lore = replaceInLore(lore, "%MEMBERS%", String.join(", ", allianceMembers));
            lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(allianceDescription));
            
            
            if (plugin.playerHasPermission(playerName, team, "seeTeamAlliances")){ 
                item = ItemBuilder.getPlayerHead(allianceOwner, itemName, lore);
            }else{
                item = ItemBuilder.getItem(
                    Material.getMaterial(config.getString("noPermission.itemType")), 
                    itemName, 
                    config.getStringList("noPermission.lore"),
                    config.getString("noPermission.headTexture"),
                    false
                );
            }

            inventory.setItem(headSlot, item);
            headSlot ++;
        }

        for(String str : config.getConfigurationSection("inventories.teamAlliances").getKeys(false)){
            Integer slot = config.getInt("inventories.teamAlliances."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.teamAlliances."+str+".itemType"));
            String name = config.getString("inventories.teamAlliances."+str+".itemName");
            String headTexture = config.getString("inventories.teamAlliances."+str+".headTexture");
            
            inventory.setItem(slot, ItemBuilder.getItem(material, name, Collections.emptyList(), headTexture, false));
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
        Material material = clickEvent.getCurrentItem().getType();
        Team team = Team.init(player);

        if (clickEvent.getView().getTitle().equals(Menus.REMOVE_ALLIANCES_MENU.getName())){
            if (material.equals(Material.getMaterial("PLAYER_HEAD"))){
                if (cacheManager.playerHasActionName(playerName, TemporaryActionNames.CLICK_REMOVE_ALLIANCE)){
                    NamespacedKey key = new NamespacedKey(StelyTeamPlugin.getPlugin(), "playerName");
                    ItemMeta meta = clickEvent.getCurrentItem().getItemMeta();
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    String ownerName = container.get(key, PersistentDataType.STRING);
                    Team alliance = Team.initFromPlayerName(ownerName);
                    UUID allianceUuid = alliance.getId();

                    if (!team.isTeamAlliance(allianceUuid)){
                        player.sendRawMessage(messageManager.getMessage("common.not_in_alliance"));
                        return;
                    }

                    cacheManager.removePlayerAction(playerName);
                    cacheManager.addTempAction(
                        new TemporaryAction(
                                playerName,
                                allianceUuid.toString(),
                                TemporaryActionNames.REMOVE_ALLIANCE,
                                team
                            )
                        );
                    new ConfirmMenu(player).openMenu();
                }
            }else if (Buttons.CLOSE_TEAM_ALLIANCES_MENU_BUTTON.isClickedButton(clickEvent)){
                new EditAlliancesMenu(player).openMenu(team);
            }

        }else{
            if (Buttons.CLOSE_TEAM_ALLIANCES_MENU_BUTTON.isClickedButton(clickEvent)){
                new MemberMenu(player).openMenu(Team.initFromPlayerName(playerName));
            }
        }
    }

    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

package fr.army.stelyteam.events.menu;

import java.util.ArrayList;
import java.util.Collections;
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

import fr.army.stelyteam.utils.Alliance;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class AlliancesInventory extends TeamMenu {

    DatabaseManager mySqlManager = plugin.getDatabaseManager();
    SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    ColorsBuilder colorsBuilder = plugin.getColorsBuilder();

    public AlliancesInventory(Player viewer){
        super(viewer);
    }


    public Inventory createInventory(Team team, String playerName) {
        Integer slots = config.getInt("inventoriesSlots.teamAlliances");
        Inventory inventory = Bukkit.createInventory(this, slots, config.getString("inventoriesName.teamAlliances"));

        emptyCases(inventory, slots, 0);
        Integer headSlot = 0;
        for(Alliance alliance : team.getTeamAlliances()){
            Team teamAlliance = mySqlManager.getTeamFromTeamName(alliance.getTeamName());
            String allianceName = teamAlliance.getTeamName();
            String alliancePrefix = teamAlliance.getTeamPrefix();
            String allianceOwnerName = teamAlliance.getTeamOwnerName();
            String allianceDate = alliance.getAllianceDate();
            Integer teamMembersLelvel = teamAlliance.getImprovLvlMembers();
            Integer teamMembers = teamAlliance.getTeamMembers().size();
            Integer maxMembers = config.getInt("teamMaxMembers");
            String allianceDescription = teamAlliance.getTeamDescription();
            ArrayList<String> allianceMembers = teamAlliance.getMembersName();
            UUID playerUUID = sqliteManager.getUUID(allianceOwnerName);
            String itemName = colorsBuilder.replaceColor(alliancePrefix);
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
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();


        // Fermeture ou retour en arrière de l'inventaire
        if (itemName.equals(config.getString("inventories.teamAlliances.close.itemName"))){
            new MemberInventory(player).openMenu(mySqlManager.getTeamFromPlayerName(playerName));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

package fr.army.stelyteam.events.inventories;

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

import fr.army.stelyteam.utils.Page;
import fr.army.stelyteam.utils.Team;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class TeamListInventory extends TeamMenu {

    final SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    final CacheManager cacheManager = plugin.getCacheManager();
    final ColorsBuilder colorsBuilder = plugin.getColorsBuilder();

    public TeamListInventory(Player viewer) {
        super(viewer);
    }


    public Inventory createInventory(String playerName) {
        ArrayList<Team> teams = plugin.getDatabaseManager().getTeams();
        Integer slots = config.getInt("inventoriesSlots.teamList");
        List<Integer> headSlots = config.getIntegerList("inventories.teamList.teamOwnerHead.slots");
        Inventory inventory = Bukkit.createInventory(this, slots, config.getString("inventoriesName.teamList"));
        Integer maxMembers = config.getInt("teamMaxMembers");
        Page page;

        if (cacheManager.containsPage(playerName)){
            page = cacheManager.getPage(playerName);
        }else{
            page = new Page(playerName, headSlots.size(), teams);
            cacheManager.addPage(page);
        }
        ArrayList<List<Team>> pages = page.getPages();
        
        emptyCases(inventory, slots, 0);
        Integer slotIndex = 0;
        for(Team team : pages.get(page.getCurrentPage())){
            String teamOwnerName = team.getTeamOwnerName();
            String teamPrefix = team.getTeamPrefix();
            UUID playerUUID = sqliteManager.getUUID(teamOwnerName);
            String itemName = colorsBuilder.replaceColor(teamPrefix);
            List<String> lore = config.getStringList("teamListLore");
            OfflinePlayer teamOwner;
            ItemStack item;

            if (!sqliteManager.isRegistered(teamOwnerName)){
                sqliteManager.registerPlayer(Bukkit.getOfflinePlayer(teamOwnerName));
            }

            if (playerUUID == null) teamOwner = Bukkit.getOfflinePlayer(teamOwnerName);
            else teamOwner = Bukkit.getOfflinePlayer(playerUUID);
            
            lore = replaceInLore(lore, "%OWNER%", teamOwnerName);
            lore = replaceInLore(lore, "%NAME%", team.getTeamName());
            lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(teamPrefix));
            lore = replaceInLore(lore, "%DATE%", team.getCreationDate());
            lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(team.getTeamMembers().size()));
            lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+team.getImprovLvlMembers()));
            lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(team.getTeamDescription()));
            
            item = ItemBuilder.getPlayerHead(teamOwner, itemName, lore);
            // item = ItemBuilder.getItem(Material.DIRT, teamOwnerName, lore, null, false);

            inventory.setItem(headSlots.get(slotIndex), item);
            slotIndex ++;
        }

        for(String str : config.getConfigurationSection("inventories.teamList").getKeys(false)){
            if (str.equals("teamOwnerHead")) continue;

            Integer slot = config.getInt("inventories.teamList."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.teamList."+str+".itemType"));
            String name = config.getString("inventories.teamList."+str+".itemName");
            List<String> lore = config.getStringList("inventories.teamList."+str+".lore");
            String headTexture = config.getString("inventories.teamList."+str+".headTexture");

            if (str.equals("previous")){
                if (page.getCurrentPage() == 0) continue;
            }else if (str.equals("next")){
                if (page.getCurrentPage() == pages.size()-1) continue;
            }

            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
        }

        return inventory;
    }


    public void openMenu(){
        this.open(createInventory(viewer.getName()));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName;
        
        if (clickEvent.getCurrentItem() != null){
            itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
            if (itemName.equals(config.getString("inventories.teamList.previous.itemName"))){
                Page page = cacheManager.getPage(playerName);
                page.setCurrentPage(page.getCurrentPage()-1);
                cacheManager.replacePage(page);
                // player.openInventory(inventoryBuilder.createTeamListInventory(playerName));
                new TeamListInventory(player).openMenu();
            }else if (itemName.equals(config.getString("inventories.teamList.next.itemName"))){
                Page page = cacheManager.getPage(playerName);
                page.setCurrentPage(page.getCurrentPage()+1);
                cacheManager.replacePage(page);
                // player.openInventory(inventoryBuilder.createTeamListInventory(playerName));
                new TeamListInventory(player).openMenu();
            }else if (itemName.equals(config.getString("inventories.teamList.close.itemName"))){
                cacheManager.removePage(cacheManager.getPage(playerName));
                player.closeInventory();
            }
        }
        clickEvent.setCancelled(true);
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

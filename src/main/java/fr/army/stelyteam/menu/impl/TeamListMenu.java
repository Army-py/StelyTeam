package fr.army.stelyteam.menu.impl;

import java.util.ArrayList;
import java.util.Collection;
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

import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.PagedMenu;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.team.Page;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class TeamListMenu extends PagedMenu {

    final DatabaseManager databaseManager = plugin.getDatabaseManager();
    final SQLiteDataManager sqliteManager = plugin.getSQLiteManager();
    final CacheManager cacheManager = plugin.getCacheManager();
    final ColorsBuilder colorsBuilder = plugin.getColorsBuilder();
    final MessageManager messageManager = plugin.getMessageManager();

    public TeamListMenu(Player viewer, TeamMenu previousMenu) {
        super(
            viewer,
            Menus.TEAM_LIST_MENU.getName(),
            Menus.TEAM_LIST_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory(String playerName, int pageId) {
        Collection<Team> teams = plugin.getDatabaseManager().getTeams();
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);
        List<Integer> headSlots = config.getIntegerList("inventories.teamList.teamOwnerHead.slots");
        Integer maxMembers = config.getInt("teamMaxMembers");
        Page page;

        if (cacheManager.containsPage(playerName)){
            page = cacheManager.getPage(playerName);
        }else{
            page = new Page(playerName, headSlots.size(), teams);
            cacheManager.addPage(page);
        }
        List<List<Team>> pages = page.getPages();
        
        List<Team> currentPage;
        if (!pages.isEmpty()) currentPage = pages.get(pageId);
        else currentPage = new ArrayList<>();
        
        emptyCases(inventory, this.menuSlots, 0);
        Integer slotIndex = 0;
        for(Team team : currentPage){
            String teamOwnerName = team.getTeamOwnerName();
            String teamPrefix = team.getTeamPrefix();
            List<String> teamMembers = team.getMembersName();
            teamMembers.remove(teamOwnerName);
            UUID playerUUID = databaseManager.getUUID(teamOwnerName);
            // String itemName = colorsBuilder.replaceColor(teamPrefix);
            String itemName = " ";
            List<String> lore = config.getStringList("teamListLore");
            // OfflinePlayer teamOwner;
            ItemStack item;

            // if (!sqliteManager.isRegistered(teamOwnerName)){
            //     sqliteManager.registerPlayer(Bukkit.getOfflinePlayer(teamOwnerName));
            // }

            // if (playerUUID == null) teamOwner = Bukkit.getOfflinePlayer(teamOwnerName);
            // else teamOwner = Bukkit.getOfflinePlayer(playerUUID);
            
            lore = replaceInLore(lore, "%OWNER%", teamOwnerName);
            lore = replaceInLore(lore, "%NAME%", team.getTeamName());
            lore = replaceInLore(lore, "%PREFIX%", colorsBuilder.replaceColor(teamPrefix));
            lore = replaceInLore(lore, "%DATE%", team.getCreationDate());
            lore = replaceInLore(lore, "%MEMBER_COUNT%", IntegerToString(team.getTeamMembers().size()));
            lore = replaceInLore(lore, "%MAX_MEMBERS%", IntegerToString(maxMembers+team.getImprovLvlMembers()));
            lore = replaceInLore(lore, "%MEMBERS%", teamMembers.isEmpty() ? messageManager.getMessageWithoutPrefix("common.no_members") : String.join(", ", teamMembers));
            lore = replaceInLore(lore, "%DESCRIPTION%", colorsBuilder.replaceColor(team.getTeamDescription()));
            
            item = ItemBuilder.getPlayerHead(playerUUID, itemName, lore);

            inventory.setItem(headSlots.get(slotIndex), item);
            slotIndex ++;
        }

        for(String buttonName : config.getConfigurationSection("inventories.teamList").getKeys(false)){
            if (buttonName.equals("teamOwnerHead")) continue;

            Integer slot = config.getInt("inventories.teamList."+buttonName+".slot");
            Material material = Material.getMaterial(config.getString("inventories.teamList."+buttonName+".itemType"));
            String displayName = config.getString("inventories.teamList."+buttonName+".itemName");
            List<String> lore = config.getStringList("inventories.teamList."+buttonName+".lore");
            String headTexture = config.getString("inventories.teamList."+buttonName+".headTexture");

            if (buttonName.equals("previous")){
                if (page.getCurrentPage() == 0) continue;
            }else if (buttonName.equals("next")){
                if (page.getCurrentPage() >= pages.size()-1) continue;
            }

            inventory.setItem(slot, ItemBuilder.getItem(material, buttonName, displayName, lore, headTexture, false));
        }

        return inventory;
    }


    @Override
    public void openMenu(int pageId){
        this.open(createInventory(viewer.getName(), pageId));
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        
        if (clickEvent.getCurrentItem() != null){
            if (Buttons.PREVIOUS_TEAM_LIST_BUTTON.isClickedButton(clickEvent)){
                Page page = cacheManager.getPage(playerName)
                    .previousPage();
                cacheManager.replacePage(page);
                new TeamListMenu(player, previousMenu).openMenu(page.getCurrentPage());
            }else if (Buttons.NEXT_TEAM_LIST_BUTTON.isClickedButton(clickEvent)){
                Page page = cacheManager.getPage(playerName)
                    .nextPage();
                cacheManager.replacePage(page);
                new TeamListMenu(player, previousMenu).openMenu(page.getCurrentPage());
            }else if (Buttons.CLOSE_TEAM_LIST_MENU_BUTTON.isClickedButton(clickEvent)){
                cacheManager.removePage(cacheManager.getPage(playerName));
                previousMenu.openMenu();
            }
        }
        clickEvent.setCancelled(true);
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {
        Player player = (Player) closeEvent.getPlayer();
        String playerName = player.getName();

        if (cacheManager.containsPage(playerName)){
            cacheManager.removePage(cacheManager.getPage(playerName));
        }
    }


    @Override
    public void openMenu() {}
}

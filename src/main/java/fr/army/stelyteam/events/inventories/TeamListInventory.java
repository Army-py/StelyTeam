package fr.army.stelyteam.events.inventories;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.Page;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;

public class TeamListInventory {
    
    private CacheManager cacheManager;
    private InventoryClickEvent clickEvent;
    private YamlConfiguration config;
    private InventoryBuilder inventoryBuilder;


    public TeamListInventory(InventoryClickEvent clickEvent, StelyTeamPlugin plugin) {
        this.clickEvent = clickEvent;
        this.cacheManager = plugin.getCacheManager();
        this.config = plugin.getConfig();
        this.inventoryBuilder = plugin.getInventoryBuilder();
    }


    public void onInventoryClick(){
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName;
        
        if (clickEvent.getCurrentItem() != null){
            itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();
            if (itemName.equals(config.getString("inventories.teamList.previous.itemName"))){
                Page page = cacheManager.getPage(playerName);
                page.setCurrentPage(page.getCurrentPage()-1);
                cacheManager.replacePage(page);
                player.openInventory(inventoryBuilder.createTeamListInventory(playerName));
            }else if (itemName.equals(config.getString("inventories.teamList.next.itemName"))){
                Page page = cacheManager.getPage(playerName);
                page.setCurrentPage(page.getCurrentPage()+1);
                cacheManager.replacePage(page);
                player.openInventory(inventoryBuilder.createTeamListInventory(playerName));
            }else if (itemName.equals(config.getString("inventories.teamList.close.itemName"))){
                cacheManager.removePage(cacheManager.getPage(playerName));
                player.closeInventory();
            }
        }
        clickEvent.setCancelled(true);
    }
}

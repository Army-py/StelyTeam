package fr.army.stelyteam.events.menu;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;


public class AdminInventory extends TeamMenu {

    DatabaseManager mySqlManager = plugin.getDatabaseManager();

    public AdminInventory(Player viewer) {
        super(viewer);
    }


    public Inventory createInventory() {
        Integer slots = config.getInt("inventoriesSlots.admin");
        Inventory inventory = Bukkit.createInventory(this, slots, config.getString("inventoriesName.admin"));

        emptyCases(inventory, slots, 0);

        for(String str : config.getConfigurationSection("inventories.admin").getKeys(false)){
            Integer slot = config.getInt("inventories.admin."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.admin."+str+".itemType"));
            String name = config.getString("inventories.admin."+str+".itemName");
            List<String> lore = config.getStringList("inventories.admin."+str+".lore");
            String headTexture = config.getString("inventories.admin."+str+".headTexture");
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
        }
        this.menu = this;
        return inventory;
    }


    public void openMenu(){
        this.open(createInventory());
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();
        String itemName = clickEvent.getCurrentItem().getItemMeta().getDisplayName();

        // Ouverture des inventaires
        if (itemName.equals(config.getString("inventories.admin.manage.itemName"))){
            // Inventory inventory = new ManageInventory(plugin).createInventory(new TeamInteraction(playerName, mySqlManager.getTeamFromPlayerName(playerName)));
            // player.openInventory(inventory);
            new ManageInventory(viewer).openMenu(mySqlManager.getTeamFromPlayerName(playerName));
        }else if (itemName.equals(config.getString("inventories.admin.member.itemName"))){
            // Inventory inventory = new MemberInventory(plugin).createInventory(new TeamInteraction(playerName, mySqlManager.getTeamFromPlayerName(playerName)));
            // player.openInventory(inventory);
            new MemberInventory(viewer).openMenu(mySqlManager.getTeamFromPlayerName(playerName));
            
        // Fermeture ou retour en arrière de l'inventaire
        }else if (itemName.equals(config.getString("inventories.admin.close.itemName"))){
            player.closeInventory();
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

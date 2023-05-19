package fr.army.stelyteam.menu.impl;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;


public class AdminMenu extends TeamMenu {

    DatabaseManager mySqlManager = plugin.getDatabaseManager();

    public AdminMenu(Player viewer) {
        super(
            viewer,
            Menus.ADMIN_MENU.getName(),
            Menus.ADMIN_MENU.getSlots()
        );
    }


    public Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);

        for(String str : config.getConfigurationSection("inventories.admin").getKeys(false)){
            Integer slot = config.getInt("inventories.admin."+str+".slot");
            Material material = Material.getMaterial(config.getString("inventories.admin."+str+".itemType"));
            String name = config.getString("inventories.admin."+str+".itemName");
            List<String> lore = config.getStringList("inventories.admin."+str+".lore");
            String headTexture = config.getString("inventories.admin."+str+".headTexture");
            inventory.setItem(slot, ItemBuilder.getItem(material, name, lore, headTexture, false));
        }
        return inventory;
    }


    public void openMenu(){
        this.open(createInventory());
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();
        String playerName = player.getName();

        // Ouverture des inventaires
        if (Buttons.MANAGE_MENU_BUTTON.isClickedButton(clickEvent)){
            new ManageMenu(viewer).openMenu(Team.initFromPlayerName(playerName));
        }else if (Buttons.TEAM_LIST_MENU_BUTTON.isClickedButton(clickEvent)){
            new TeamListMenu(viewer).openMenu();
        }else if (Buttons.MEMBER_MENU_BUTTON.isClickedButton(clickEvent)){
            new MemberMenu(viewer).openMenu(Team.initFromPlayerName(playerName));
            
        // Fermeture ou retour en arrière de l'inventaire
        }else if (Buttons.CLOSE_ADMIN_MENU_BUTTON.isClickedButton(clickEvent)){
            player.closeInventory();
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

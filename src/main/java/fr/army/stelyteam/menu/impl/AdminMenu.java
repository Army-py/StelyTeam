package fr.army.stelyteam.menu.impl;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.FixedMenu;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.utils.builder.ItemBuilder;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;


public class AdminMenu extends FixedMenu {

    DatabaseManager mySqlManager = plugin.getDatabaseManager();

    public AdminMenu(Player viewer, TeamMenu previousMenu) {
        super(
            viewer,
            Menus.ADMIN_MENU.getName(),
            Menus.ADMIN_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);

        for(String buttonName : config.getConfigurationSection("inventories.admin").getKeys(false)){
            Integer slot = config.getInt("inventories.admin."+buttonName+".slot");
            Material material = Material.getMaterial(config.getString("inventories.admin."+buttonName+".itemType"));
            String displayName = config.getString("inventories.admin."+buttonName+".itemName");
            List<String> lore = config.getStringList("inventories.admin."+buttonName+".lore");
            String headTexture = config.getString("inventories.admin."+buttonName+".headTexture");
            inventory.setItem(slot, ItemBuilder.getItem(material, buttonName, displayName, lore, headTexture, false));
        }
        return inventory;
    }


    @Override
    public void openMenu(){
        this.open(createInventory());
    }


    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        Player player = (Player) clickEvent.getWhoClicked();

        // Ouverture des inventaires
        if (Buttons.MANAGE_MENU_BUTTON.isClickedButton(clickEvent)){
            new ManageMenu(viewer, this).openMenu();
        }else if (Buttons.TEAM_LIST_MENU_BUTTON.isClickedButton(clickEvent)){
            new TeamListMenu(viewer, this).openMenu(0);
        }else if (Buttons.MEMBER_MENU_BUTTON.isClickedButton(clickEvent)){
            new MemberMenu(viewer, this).openMenu();
            
        // Fermeture ou retour en arrière de l'inventaire
        }else if (Buttons.CLOSE_ADMIN_MENU_BUTTON.isClickedButton(clickEvent)){
            player.closeInventory();
        }
    }


    @Override
    public void onClose(InventoryCloseEvent closeEvent) {}
}

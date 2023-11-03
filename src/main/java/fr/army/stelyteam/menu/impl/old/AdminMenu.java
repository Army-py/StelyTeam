package fr.army.stelyteam.menu.impl.old;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.menu.FixedMenuOLD;
import fr.army.stelyteam.menu.MenusOLD;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.utils.builder.ItemBuilderOLD;


public class AdminMenu extends FixedMenuOLD {

    public AdminMenu(Player viewer, TeamMenuOLD previousMenu) {
        super(
            viewer,
            MenusOLD.ADMIN_MENU.getName(),
            MenusOLD.ADMIN_MENU.getSlots(),
            previousMenu
        );
    }


    public Inventory createInventory(String playerName) {
        Inventory inventory = Bukkit.createInventory(this, this.menuSlots, this.menuName);

        emptyCases(inventory, this.menuSlots, 0);

        for(String buttonName : config.getConfigurationSection("inventories.admin").getKeys(false)){
            Integer slot = config.getInt("inventories.admin."+buttonName+".slot");
            Material material = Material.getMaterial(config.getString("inventories.admin."+buttonName+".itemType"));
            String displayName = config.getString("inventories.admin."+buttonName+".itemName");
            List<String> lore = config.getStringList("inventories.admin."+buttonName+".lore");
            String headTexture = config.getString("inventories.admin."+buttonName+".headTexture");
            ItemStack item;

            if (plugin.playerHasPermissionInSection(playerName, team, buttonName)
                || plugin.playerHasPermission(playerName, team, buttonName)){
                    item = ItemBuilderOLD.getItem(material, buttonName, displayName, lore, headTexture, false);
            }else{
                item = ItemBuilderOLD.getItem(
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

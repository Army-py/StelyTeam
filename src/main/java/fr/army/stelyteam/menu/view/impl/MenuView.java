package fr.army.stelyteam.menu.view.impl;

import fr.army.stelyteam.menu.view.AbstractMenuView;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.template.MenuTemplate;

public class MenuView extends AbstractMenuView<MenuView> {

    public MenuView(Player player, TeamMenu<MenuView> menu) {
        super(player, menu);
    }    
    
    @Override
    public Inventory createInventory() {
        final MenuTemplate<MenuView> menuTemplate = menu.getMenuBuilderResult().getMenuTemplate();
        final Inventory inventory = Bukkit.createInventory(this, menuTemplate.getSize(), menuTemplate.getTitle());

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            final Button<MenuView> button = menuTemplate.getButton(slot).setMenuView(this);
            final ItemStack itemStack = button.getButtonTemplate().getButtonItem().build();
            inventory.setItem(slot, itemStack);
        }

        return inventory;
    }
}

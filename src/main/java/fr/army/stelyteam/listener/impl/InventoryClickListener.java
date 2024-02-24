package fr.army.stelyteam.listener.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.menu.TeamMenuOLD;
import fr.army.stelyteam.menu.button.Buttons;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;


public class InventoryClickListener implements Listener{    

    private DatabaseManager databaseManager;

    public InventoryClickListener(StelyTeamPlugin plugin) {
        this.databaseManager = plugin.getDatabaseManager();
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event){
        if (!(event.getView().getTopInventory().getHolder() instanceof TeamMenuOLD)) {
            return;
        }

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())){
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() != null && Buttons.EMPTY_CASE.isEmptyCase(event)){
            return;
        }

        Player player = (Player) event.getWhoClicked();
        // if (!sqliteManager.isRegistered(player.getName())) sqliteManager.registerPlayer(player);
        if (!databaseManager.isRegistered(player.getName())) databaseManager.registerPlayer(player);

        ((TeamMenuOLD) event.getView().getTopInventory().getHolder()).onClick(event);
        return;
    }

}

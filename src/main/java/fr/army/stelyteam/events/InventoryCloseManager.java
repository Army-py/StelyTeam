package fr.army.stelyteam.events;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.TeamMenu;

public class InventoryCloseManager implements Listener {

    public InventoryCloseManager(StelyTeamPlugin plugin) {
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {       
        if (event.getView().getTopInventory().getHolder() instanceof TeamMenu) {
            ((TeamMenu) event.getView().getTopInventory().getHolder()).onClose(event);
            return;
        }
    }
}

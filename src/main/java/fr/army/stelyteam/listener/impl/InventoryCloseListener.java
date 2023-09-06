package fr.army.stelyteam.listener.impl;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.menu.TeamMenuOLD;

public class InventoryCloseListener implements Listener {

    public InventoryCloseListener(StelyTeamPlugin plugin) {
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {       
        if (event.getView().getTopInventory().getHolder() instanceof TeamMenuOLD) {
            ((TeamMenuOLD) event.getView().getTopInventory().getHolder()).onClose(event);
            return;
        }
    }
}

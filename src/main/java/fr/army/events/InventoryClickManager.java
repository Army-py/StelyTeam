package fr.army.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.App;
import fr.army.events.InventoryClick.AdminInventory;
import fr.army.events.InventoryClick.ConfirmInventory;
import fr.army.events.InventoryClick.CreateTeamInventory;
import fr.army.events.InventoryClick.ManageInventory;
import fr.army.events.InventoryClick.MemberInventory;


public class InventoryClickManager implements Listener{    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getCurrentItem() == null || !App.config.getConfigurationSection("inventoriesName").getValues(true).containsValue(event.getView().getTitle())){
            return;
        }

        event.setCancelled(true);
        if (event.getView().getTitle().equals(App.config.getString("inventoriesName.admin"))){
            new AdminInventory(event).onInventoryClick();
        }else if (event.getView().getTitle().equals(App.config.getString("inventoriesName.confirmInventory"))){
            new ConfirmInventory(event).onInventoryClick();
        }else if (event.getView().getTitle().equals(App.config.getString("inventoriesName.createTeam"))){
            new CreateTeamInventory(event).onInventoryClick();
        }else if (event.getView().getTitle().equals(App.config.getString("inventoriesName.manage"))){
            new ManageInventory(event).onInventoryClick();
        }else if (event.getView().getTitle().equals(App.config.getString("inventoriesName.member"))){
            new MemberInventory(event).onInventoryClick();
        }
    }
}

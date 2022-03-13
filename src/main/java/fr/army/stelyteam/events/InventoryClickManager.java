package fr.army.stelyteam.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.InventoryClick.AdminInventory;
import fr.army.stelyteam.events.InventoryClick.ConfirmInventory;
import fr.army.stelyteam.events.InventoryClick.CreateTeamInventory;
import fr.army.stelyteam.events.InventoryClick.ManageInventory;
import fr.army.stelyteam.events.InventoryClick.MemberInventory;
import fr.army.stelyteam.events.InventoryClick.UpgradeMembersInventory;


public class InventoryClickManager implements Listener{    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getCurrentItem() == null || !StelyTeamPlugin.config.getConfigurationSection("inventoriesName").getValues(true).containsValue(event.getView().getTitle())){
            return;
        }

        event.setCancelled(true);
        if (event.getView().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.admin"))){
            new AdminInventory(event).onInventoryClick();
        }else if (event.getView().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.confirmInventory"))){
            new ConfirmInventory(event).onInventoryClick();
        }else if (event.getView().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.createTeam"))){
            new CreateTeamInventory(event).onInventoryClick();
        }else if (event.getView().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.manage"))){
            new ManageInventory(event).onInventoryClick();
        }else if (event.getView().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.member"))){
            new MemberInventory(event).onInventoryClick();
        }else if (event.getView().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.upgradeTotalMembers"))){
            new UpgradeMembersInventory(event).onInventoryClick();
        }
    }
}

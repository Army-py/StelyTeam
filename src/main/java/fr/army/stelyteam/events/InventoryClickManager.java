package fr.army.stelyteam.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.events.inventoryclick.*;


public class InventoryClickManager implements Listener{    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getCurrentItem() == null || !StelyTeamPlugin.config.getConfigurationSection("inventoriesName").getValues(true).containsValue(event.getView().getTitle())){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        if (!StelyTeamPlugin.sqliteManager.isRegistered(player.getName())) StelyTeamPlugin.sqliteManager.registerPlayer(player);

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
        }else if (event.getView().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.editMembers"))){
            new EditMembersInventory(event).onInventoryClick();
        }else if (event.getView().getTitle().equals(StelyTeamPlugin.config.getString("inventoriesName.teamMembers"))){
            new MembersInventory(event).onInventoryClick();
        }
    }
}

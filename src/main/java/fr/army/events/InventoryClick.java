package fr.army.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.App;

public class InventoryClick implements Listener{
    public static Listener listener;
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getCurrentItem() == null && !App.config.getStringList("inventoriesName").contains(event.getView().getTitle())){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(App.config.getString("createTeam.itemName"))){
            player.closeInventory();
            
            listener = new PlayerChat(player.getName());
            App.instance.getServer().getPluginManager().registerEvents(listener, App.instance);
        }
    }
}

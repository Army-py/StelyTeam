package fr.army.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.App;
import fr.army.utils.InventoryGenerator;

public class InventoryClick implements Listener{    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getCurrentItem() == null || !App.config.getConfigurationSection("inventoriesName").getValues(true).containsValue(event.getView().getTitle())){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(App.config.getString("createTeam.itemName"))){
            player.closeInventory();
            App.playersCreateTeam.add(player.getName());
            player.sendMessage("Envoie un nom de team");

            App.instance.getServer().getPluginManager().registerEvents(new PlayerChat(player.getName()), App.instance);
        }else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(App.config.getString("admin.manage.itemName"))){
            Inventory inventory = InventoryGenerator.createManageInventory();
            player.openInventory(inventory);
        }else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(App.config.getString("admin.member.itemName"))){
            Inventory inventory = InventoryGenerator.createMemberInventory();
            player.openInventory(inventory);
        }
    }
}

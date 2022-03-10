package fr.army.events.InventoryClick;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import fr.army.App;
import fr.army.events.PlayerChat;
import fr.army.utils.InventoryGenerator;

public class ConfirmInventory {
    private InventoryClickEvent event;

    public ConfirmInventory(InventoryClickEvent event) {
        this.event = event;
    }


    public void onInventoryClick(){
        Player player = (Player) event.getWhoClicked();
        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        System.out.println(App.playersCreateTeam);
        System.out.println(App.playersJoinTeam);
        System.out.println(App.teamsJoinTeam);

        // Ouverture des inventaires
        if (App.playersCreateTeam.contains(player.getName())){
            if (itemName.equals(App.config.getString("confirmInventory.confirm.itemName"))){
                player.closeInventory();
                player.sendMessage("Envoie un nom de team");
                App.instance.getServer().getPluginManager().registerEvents(new PlayerChat(player.getName()), App.instance);
            }else if (itemName.equals(App.config.getString("confirmInventory.cancel.itemName"))){
                App.playersCreateTeam.remove(player.getName());
                Inventory inventory = InventoryGenerator.createTeamInventory();
                player.openInventory(inventory);
            }
        }


        if (App.playersJoinTeam.contains(player.getName())){
            if (itemName.equals(App.config.getString("confirmInventory.confirm.itemName"))){
                player.closeInventory();
                App.sqlManager.insertMember(player.getName(), App.teamsJoinTeam.get(App.playersJoinTeam.indexOf(player.getName())));
                player.sendMessage("Vous avez rejoint la team " + App.teamsJoinTeam.get(App.playersJoinTeam.indexOf(player.getName())));
                App.teamsJoinTeam.remove(App.playersJoinTeam.indexOf(player.getName()));
                App.playersJoinTeam.remove(player.getName());
            }else if (itemName.equals(App.config.getString("confirmInventory.cancel.itemName"))){
                player.closeInventory();
                App.teamsJoinTeam.remove(App.playersJoinTeam.indexOf(player.getName()));
                App.playersJoinTeam.remove(player.getName());
            }
        }
    }
}

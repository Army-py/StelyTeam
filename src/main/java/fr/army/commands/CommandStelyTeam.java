package fr.army.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.App;
import fr.army.utils.InventoryGenerator;

public class CommandStelyTeam implements CommandExecutor {
    // private PlayerChat playerChat;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            
            if(App.playersCreateTeam.contains(player.getName())){
                return true;
            }

            if(!App.sqlManager.isMember(player.getName())){
                Inventory inventory = InventoryGenerator.createTeamInventory();
                player.openInventory(inventory);
            }else if(App.sqlManager.isOwner(player.getName())){
                Inventory inventory = InventoryGenerator.createAdminInventory();
                player.openInventory(inventory);
            }
        }

    return true;
    }
}

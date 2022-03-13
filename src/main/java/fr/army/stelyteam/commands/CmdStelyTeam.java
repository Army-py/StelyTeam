package fr.army.stelyteam.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.InventoryGenerator;

public class CmdStelyTeam implements CommandExecutor, TabCompleter {
    // private PlayerChat playerChat;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            
            if(StelyTeamPlugin.playersCreateTeam.contains(player.getName())){
                return true;
            }
            
            if (args.length == 0){
                if(StelyTeamPlugin.sqlManager.isOwner(player.getName())){
                    Inventory inventory = InventoryGenerator.createAdminInventory();
                    player.openInventory(inventory);
                }else if (StelyTeamPlugin.sqlManager.isMember(player.getName())){
                    Inventory inventory = InventoryGenerator.createMemberInventory();
                    player.openInventory(inventory);
                }else if (StelyTeamPlugin.sqlManager.isAdmin(player.getName())){
                    Inventory inventory = InventoryGenerator.createAdminInventory();
                    player.openInventory(inventory);
                }else{
                    Inventory inventory = InventoryGenerator.createTeamInventory();
                    player.openInventory(inventory);
                }
            }else{
                if (args[0].equals("home")){
                    String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromOwner(player.getName());
                    if (!StelyTeamPlugin.sqliteManager.isSet(teamID)){
                        player.sendMessage("Le home de team n'est pas défini");
                    }else{
                        World world = Bukkit.getWorld(StelyTeamPlugin.sqliteManager.getWorld(teamID));
                        double x = StelyTeamPlugin.sqliteManager.getX(teamID);
                        double y = StelyTeamPlugin.sqliteManager.getY(teamID);
                        double z = StelyTeamPlugin.sqliteManager.getZ(teamID);
                        float yaw = (float) StelyTeamPlugin.sqliteManager.getYaw(teamID);
                        Location location = new Location(world, x, y, z, yaw, 0);
                        player.teleport(location);
                        player.sendMessage("Téléportation au home");
                    }
                }
            }
        }

    return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subCommands = new ArrayList<>();
        subCommands.add("home");

        if (args.length == 1){
            List<String> result = new ArrayList<>();
            for (String subcommand : subCommands) {
                if (subcommand.toLowerCase().toLowerCase().startsWith(args[0])){
                    result.add(subcommand);
                }
            }
            return result;
        }

        return null;
    }
}
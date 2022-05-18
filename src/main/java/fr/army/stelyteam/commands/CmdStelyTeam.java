package fr.army.stelyteam.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.ColorsCreator;
import fr.army.stelyteam.utils.InventoryGenerator;

public class CmdStelyTeam implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            String playerName = player.getName();
            
            if(StelyTeamPlugin.playersCreateTeam.contains(player.getName())){
                return true;
            }

            if (!StelyTeamPlugin.sqliteManager.isRegistered(player.getName())) {
                StelyTeamPlugin.sqliteManager.registerPlayer(player);
            }
            
            if (args.length == 0){
                Inventory inventory;
                if (!StelyTeamPlugin.sqlManager.isMember(playerName)){
                    inventory = InventoryGenerator.createTeamInventory();
                }else if(StelyTeamPlugin.sqlManager.isOwner(player.getName())){
                    inventory = InventoryGenerator.createAdminInventory();
                }else if (StelyTeamPlugin.sqlManager.getMemberRank(playerName) <= 3){
                    inventory = InventoryGenerator.createAdminInventory();
                }else if (StelyTeamPlugin.sqlManager.getMemberRank(playerName) >= 4){
                    inventory = InventoryGenerator.createMemberInventory(playerName);
                }else inventory = InventoryGenerator.createTeamInventory();
                player.openInventory(inventory);
            }else{
                if (args[0].equals("home")){
                    String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(player.getName());
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
                }else if (args[0].equals("visual")){
                    args[0] = "";
                    player.sendMessage("Ton texte :" + new ColorsCreator().colourise(String.join(" ", args)));
                }
            }
        }

    return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subCommands = new ArrayList<>();
        subCommands.add("home");
        subCommands.add("visual");

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

package fr.army.stelyteam.commands.subCommands.utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.SQLManager;
import fr.army.stelyteam.utils.manager.SQLiteManager;

public class SubCmdHome extends SubCommand {

    private SQLManager sqlManager;
    private SQLiteManager sqliteManager;
    private MessageManager messageManager;

    public SubCmdHome(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getSQLManager();
        this.sqliteManager = plugin.getSQLiteManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String teamID = sqlManager.getTeamNameFromPlayerName(player.getName());
        
        if (!sqliteManager.isSet(teamID)){
            // player.sendMessage("Le home de team n'est pas défini");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_home.not_set"));
        }else{
            World world = Bukkit.getWorld(sqliteManager.getWorld(teamID));
            double x = sqliteManager.getX(teamID);
            double y = sqliteManager.getY(teamID);
            double z = sqliteManager.getZ(teamID);
            float yaw = (float) sqliteManager.getYaw(teamID);
            Location location = new Location(world, x, y, z, yaw, 0);
            // player.sendMessage("Téléportation au home");
            player.sendMessage(messageManager.getMessage("commands.stelyteam_home.teleport"));
            player.teleport(location);
        }
        return true;
    }

}

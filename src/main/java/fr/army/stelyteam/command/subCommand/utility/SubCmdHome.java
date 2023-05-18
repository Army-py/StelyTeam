package fr.army.stelyteam.command.subCommand.utility;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

public class SubCmdHome extends SubCommand {

    private DatabaseManager sqlManager;
    private SQLiteDataManager sqliteManager;
    private MessageManager messageManager;

    public SubCmdHome(StelyTeamPlugin plugin) {
        super(plugin);
        this.sqlManager = plugin.getDatabaseManager();
        this.sqliteManager = plugin.getSQLiteManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String teamID = sqlManager.getTeamNameFromPlayerName(player.getName());
        
        if (!sqliteManager.isSet(teamID)){
            player.sendMessage(messageManager.getMessage("commands.stelyteam_home.not_set"));
        }else{
            World world = Bukkit.getWorld(sqliteManager.getWorld(teamID));
            double x = sqliteManager.getX(teamID);
            double y = sqliteManager.getY(teamID);
            double z = sqliteManager.getZ(teamID);
            float yaw = (float) sqliteManager.getYaw(teamID);
            Location location = new Location(world, x, y, z, yaw, 0);
            player.sendMessage(messageManager.getMessage("commands.stelyteam_home.teleport"));
            player.teleport(location);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isOpCommand() {
        return false;
    }

}

package fr.army.stelyteam.command.subcommand.utility;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.cache.StorageManager;
import fr.army.stelyteam.command.SubCommand;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SubCmdHome extends SubCommand {

    //private DatabaseManager sqlManager;
    private final StorageManager storageManager;
    private final SQLiteDataManager sqliteManager;
    private final MessageManager messageManager;

    public SubCmdHome(@NotNull StelyTeamPlugin plugin) {
        super(plugin);
        storageManager = plugin.getStorageManager();
        //this.sqlManager = plugin.getDatabaseManager();
        this.sqliteManager = plugin.getSQLiteManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull String @NotNull [] args) {
        final Team team = storageManager.retreivePlayerTeam(player.getName());
        if (team == null) {
            player.sendMessage(messageManager.getMessage("common.team_not_exist"));
            return true;
        }

        final UUID teamId = team.getId();

        if (!sqliteManager.isSet(teamId)) {
            player.sendMessage(messageManager.getMessage("commands.stelyteam_home.not_set"));
            return true;
        }
        World world = Bukkit.getWorld(sqliteManager.getWorld(teamId));
        double x = sqliteManager.getX(teamId);
        double y = sqliteManager.getY(teamId);
        double z = sqliteManager.getZ(teamId);
        float yaw = (float) sqliteManager.getYaw(teamId);
        Location location = new Location(world, x, y, z, yaw, 0);
        player.sendMessage(messageManager.getMessage("commands.stelyteam_home.teleport"));
        player.teleport(location);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean isOpCommand() {
        return false;
    }

}

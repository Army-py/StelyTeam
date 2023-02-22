package fr.army.stelyteam.commands.subCommands.info;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.events.inventories.CreateTeamInventory;
import fr.army.stelyteam.utils.manager.CacheManager;

public class SubCmdList extends SubCommand {

    private CacheManager cacheManager;

    public SubCmdList(StelyTeamPlugin plugin) {
        super(plugin);
        this.cacheManager = plugin.getCacheManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String playerName = player.getName();

        cacheManager.removePage(cacheManager.getPage(playerName));
        
        // Inventory inventory = inventoryBuilder.createTeamListInventory(playerName);
        // player.openInventory(inventory);
        new CreateTeamInventory(player).openMenu();

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

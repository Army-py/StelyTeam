package fr.army.stelyteam.commands.subCommands.info;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.commands.SubCommand;
import fr.army.stelyteam.utils.builder.InventoryBuilder;
import fr.army.stelyteam.utils.manager.CacheManager;

public class SubCmdList extends SubCommand {

    private InventoryBuilder inventoryBuilder;
    private CacheManager cacheManager;

    public SubCmdList(StelyTeamPlugin plugin) {
        super(plugin);
        this.inventoryBuilder = new InventoryBuilder(plugin);
        this.cacheManager = plugin.getCacheManager();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String playerName = player.getName();

        cacheManager.removePage(cacheManager.getPage(playerName));
        
        Inventory inventory = inventoryBuilder.createTeamListInventory(playerName);
        player.openInventory(inventory);

        return true;
    }
    
}
package fr.army.stelyteam.events;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.TeamMenu;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class InventoryClickManager implements Listener{    

    private SQLiteDataManager sqliteManager;
    private YamlConfiguration config;

    public InventoryClickManager(StelyTeamPlugin plugin) {
        this.sqliteManager = plugin.getSQLiteManager();
        this.config = plugin.getConfig();
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event){
        if (!(event.getView().getTopInventory().getHolder() instanceof TeamMenu)) {
            return;
        }

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())){
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() != null && event.getCurrentItem().getType().name().equals(config.getString("emptyCase"))){
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (!sqliteManager.isRegistered(player.getName())) sqliteManager.registerPlayer(player);

        ((TeamMenu) event.getView().getTopInventory().getHolder()).onClick(event);
        return;
    }
}

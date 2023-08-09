package fr.army.stelyteam.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.menu.Buttons;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;
import fr.army.stelyteam.utils.manager.database.SQLiteDataManager;


public class InventoryClickListener implements Listener{    

    private StelyTeamPlugin plugin;
    private DatabaseManager databaseManager;
    private SQLiteDataManager sqliteManager;

    public InventoryClickListener(StelyTeamPlugin plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        this.sqliteManager = plugin.getSQLiteManager();
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event){
        if (!(event.getView().getTopInventory().getHolder() instanceof TeamMenu)) {
            return;
        }

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())){
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() != null && Buttons.EMPTY_CASE.isEmptyCase(event)){
            return;
        }

        Player player = (Player) event.getWhoClicked();
        // if (!sqliteManager.isRegistered(player.getName())) sqliteManager.registerPlayer(player);
        if (!databaseManager.isRegistered(player.getName())) databaseManager.registerPlayer(player);

        ((TeamMenu) event.getView().getTopInventory().getHolder()).onClick(event);
        return;
    }

}

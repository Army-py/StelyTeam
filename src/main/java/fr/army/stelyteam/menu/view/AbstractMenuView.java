package fr.army.stelyteam.menu.view;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.template.MenuTemplate;
import fr.army.stelyteam.team.Team;

public abstract class AbstractMenuView<T extends AbstractMenuView<T>> implements IMenuView {

    private final StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    
    protected final Player viewer;
    protected final TeamMenu<T> menu;

    protected Inventory inventory;

    public AbstractMenuView(@NotNull Player player, @NotNull TeamMenu<T> menu) {
        this.viewer = player;
        this.menu = menu;
    }


    public abstract Inventory createInventory();


    public void open(){
        viewer.openInventory(createInventory());
    }

    public void onClose(InventoryCloseEvent closeEvent){
        final Player player = (Player) closeEvent.getPlayer();
        final MenuTemplate<T> menuTemplate = menu.getMenuBuilderResult().getMenuTemplate();
        final TeamMenu<T> precedingMenu = menuTemplate.getPrecedingMenu();

        final Optional<Team> team;
        if (viewer.getOpenInventory().getTopInventory().getHolder() instanceof TeamMenuView)
            team = Optional.of(((TeamMenuView) viewer.getOpenInventory().getTopInventory().getHolder()).getTeam());
        else
            team = Optional.empty();

        if (menuTemplate.canPrecede() && precedingMenu != null){
            new BukkitRunnable() {
                @Override
                public void run(){
                    precedingMenu.createView(player, team);
                }
            }.runTaskLater(plugin, 1);
        }
    }


    public Player getViewer() {
        return viewer;
    }

    public TeamMenu<T> getMenu() {
        return menu;
    }

    public Inventory getInventory() {
        return inventory;
    }
}

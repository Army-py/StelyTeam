package fr.army.stelyteam.menu.view;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.template.MenuTemplate;
import fr.army.stelyteam.team.Team;

public class TeamMenuView extends AbstractMenuView<TeamMenuView> {
    
    private final Team team;

    public TeamMenuView(Player player, TeamMenu<TeamMenuView> menu, Team team) {
        super(player, menu);
        this.team = team;
    }

    @Override
    public Inventory createInventory() {
        final MenuTemplate<TeamMenuView> menuTemplate = menu.getMenuBuilderResult().getMenuTemplate();
        final Inventory inventory = Bukkit.createInventory(this, menuTemplate.getSize(), menuTemplate.getTitle());

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            final Button<TeamMenuView> button = menuTemplate.getButton(slot).setMenuView(this);
            final ItemStack itemStack = button.getButtonTemplate().getButtonItem().build();
            inventory.setItem(slot, itemStack);
        }

        return inventory;
    }

    public Team getTeam() {
        return team;
    }
}

package fr.army.stelyteam.menu.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.PlaceholdersUtils;
import fr.army.stelyteam.config.message.Placeholders;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.ButtonItem;
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
            final ButtonItem buttonItem = button.getButtonTemplate().getButtonItem();
            final ItemStack itemStack = buttonItem.build();

            List<String> lore = buttonItem.getLore();
            
            Map<Placeholders, String> replaceMap = new HashMap<>();
            replaceMap.put(Placeholders.TEAM_NAME, team.getTeamName());
            replaceMap.put(Placeholders.TEAM_PREFIX, team.getTeamPrefix());
            replaceMap.put(Placeholders.TEAM_DESCRIPTION, team.getTeamDescription());
            replaceMap.put(Placeholders.TEAM_OWNER_NAME, team.getTeamOwner().getMemberName());
            replaceMap.put(Placeholders.TEAM_OWNER_DISPLAY_NAME, team.getTeamOwner().asPlayer().getDisplayName()); // TODO: revoir
            replaceMap.put(Placeholders.TEAM_MEMBER_COUNT, ((Integer) team.getTeamMembers().size()).toString());
            replaceMap.put(Placeholders.TEAM_CREATION_DATE, team.getCreationDate());
            replaceMap.put(Placeholders.MEMBER_RANK, null); // TODO: ajouter rank member (revoir cache, Member, Rank, Team)
            replaceMap.put(Placeholders.CONFIG_MAX_MEMBERS_COUNT, ((Integer) Config.teamMaxMembersLimit).toString());

            replaceMap.put(Placeholders.TEAM_MONEY, ((Double) team.getTeamMoney()).toString());
            replaceMap.put(Placeholders.CONFIG_MAX_MONEY_LIMIT, ((Double) Config.teamBankMaxMoneyLimit).toString());
            buttonItem.setLore(PlaceholdersUtils.replaceList(lore, replaceMap));

            inventory.setItem(slot, itemStack);
        }

        return inventory;
    }

    public Team getTeam() {
        return team;
    }
}

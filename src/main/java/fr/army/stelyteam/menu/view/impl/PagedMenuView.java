package fr.army.stelyteam.menu.view.impl;

import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.PlaceholdersUtils;
import fr.army.stelyteam.config.message.Placeholders;
import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.ButtonItem;
import fr.army.stelyteam.menu.button.ComponentButton;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.template.MenuTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.page.PageBuilderResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagedMenuView<C> extends AbstractMenuView<PagedMenuView<C>> {

    private final Team team;
    private final PageBuilderResult<C> pageBuilderResult;

    public PagedMenuView(@NotNull Player player, @NotNull TeamMenu<PagedMenuView<C>> menu, @Nullable Team team,
                         @NotNull PageBuilderResult<C> pageBuilderResult) {
        super(player, menu);
        this.team = team;
        this.pageBuilderResult = pageBuilderResult;
    }

    @Override
    public Inventory createInventory() {
        final MenuTemplate<PagedMenuView<C>> menuTemplate = menu.getMenuBuilderResult().getMenuTemplate();
        final Inventory inventory = Bukkit.createInventory(this, menuTemplate.getSize(), menuTemplate.getTitle());

        int componentPage = 0;
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            final Button<PagedMenuView<C>> button = menuTemplate.getButton(slot).setMenuView(this);
            final ButtonTemplate buttonTemplate = button.getButtonTemplate();
            final ButtonItem buttonItem = buttonTemplate.getButtonItem();


            if (button instanceof ComponentButton<PagedMenuView<C>, C> componentButton) {
                if (pageBuilderResult.getPage(0) != null){
                    List<C> page;
                    if ((page = pageBuilderResult.getPage(0)) != null){
                        componentButton.replacePlaceholders(page.get(componentPage));
                    }
                    // TODO: remplacer le 0 dans getPage par un get de la page actuelle (mettre un cache qlq part), à faire
                    // après merge la branche cache
                    componentPage++;
                }
            }


            List<String> lore = buttonItem.getLore();

            Map<Placeholders, String> replaceMap = new HashMap<>();
            replaceMap.put(Placeholders.TEAM_NAME, team.getTeamName());
            replaceMap.put(Placeholders.TEAM_PREFIX, team.getTeamPrefix());
            replaceMap.put(Placeholders.TEAM_DESCRIPTION, team.getTeamDescription());
            replaceMap.put(Placeholders.TEAM_OWNER_NAME, team.getTeamOwner().getMemberName());
            replaceMap.put(Placeholders.TEAM_OWNER_DISPLAY_NAME, team.getTeamOwner().asPlayer().getDisplayName()); // TODO: revoir
            replaceMap.put(Placeholders.TEAM_MEMBERS_COUNT, ((Integer) team.getTeamMembers().size()).toString());
            replaceMap.put(Placeholders.TEAM_CREATION_DATE, team.getCreationDate());
            replaceMap.put(Placeholders.MEMBER_RANK, null); // TODO: ajouter rank member (revoir cache, Member, Rank, Team)
            replaceMap.put(Placeholders.CONFIG_MAX_MEMBERS_COUNT, ((Integer) Config.teamMaxMembersLimit).toString());

            replaceMap.put(Placeholders.TEAM_MONEY, ((Double) team.getTeamMoney()).toString());
            replaceMap.put(Placeholders.CONFIG_MAX_MONEY_LIMIT, ((Double) Config.teamBankMaxMoneyLimit).toString());
            buttonItem.setLore(PlaceholdersUtils.replaceList(lore, replaceMap));

            inventory.setItem(slot, buttonItem.build());
        }

        return inventory;
    }
}

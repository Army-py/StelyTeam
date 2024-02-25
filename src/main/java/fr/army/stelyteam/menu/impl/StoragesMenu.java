package fr.army.stelyteam.menu.impl;

import fr.army.stelyteam.menu.TeamMenu;
import fr.army.stelyteam.menu.view.impl.PagedMenuView;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.menu.MenuBuilder;
import fr.army.stelyteam.utils.builder.menu.MenuBuilderResult;
import fr.army.stelyteam.utils.builder.page.PageBuilder;
import fr.army.stelyteam.utils.builder.page.PageBuilderResult;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class StoragesMenu<C> extends TeamMenu<PagedMenuView<C>> {
    public StoragesMenu(@NotNull MenuBuilderResult<PagedMenuView<C>> menuBuilderResult) {
        super(menuBuilderResult);
    }

    @Override
    public PagedMenuView<C> createView(Player player, Optional<Team> optionalTeam) {
        final Team team = optionalTeam.orElse(null);
        final PageBuilderResult<C> pageBuilderResult;
        if (team == null){
            pageBuilderResult = PageBuilder.getInstance().buildEmptyPage();
        }else{
            // TODO: fixe ici (une fois merge la branche cache, utiliser les nouveaux types et les étendres à une interface commune)
            pageBuilderResult = PageBuilder.getInstance().buildPage((List<C>) team.getTeamStorages(), menuBuilderResult.getMenuTemplate().getComponentCount());
        }

        return new PagedMenuView<>(player, this, team, pageBuilderResult);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {

    }

    public static <C> StoragesMenu<C> createInstance(String configName){
        final MenuBuilderResult<PagedMenuView<C>> builderResult = MenuBuilder.getInstance().loadMenu(configName + ".yml");
        return new StoragesMenu<>(builderResult);
    }
}

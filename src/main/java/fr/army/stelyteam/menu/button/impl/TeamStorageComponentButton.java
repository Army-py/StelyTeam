package fr.army.stelyteam.menu.button.impl;

import fr.army.stelyteam.config.PlaceholdersUtils;
import fr.army.stelyteam.config.message.Placeholders;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.ButtonItem;
import fr.army.stelyteam.menu.button.ComponentButton;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import fr.army.stelyteam.menu.view.impl.PagedMenuView;
import fr.army.stelyteam.team.Member;
import fr.army.stelyteam.team.Storage;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TeamStorageComponentButton<C> extends ComponentButton<PagedMenuView<C>, C> {
    public TeamStorageComponentButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {

    }

    @Override
    public @NotNull Button<? extends AbstractMenuView<?>> get(@NotNull ButtonTemplate buttonTemplate) {
        return new TeamStorageComponentButton<>(buttonTemplate);
    }

    @Override
    public void replacePlaceholders(C component) {
//        if (!(component instanceof Storage storage)) {
//            return;
//        }
//
//        final ButtonItem buttonItem = buttonTemplate.getButtonItem();
//
//        Map<Placeholders, String> replaceMap = new HashMap<>();
//        replaceMap.put(Placeholders.TEAM_MEMBER_NAME, member.getMemberName());
//        replaceMap.put(Placeholders.TEAM_MEMBER_DISPLAY_NAME, member.asPlayer().getDisplayName());
//        replaceMap.put(Placeholders.TEAM_MEMBER_RANK, member.getTeamRank().getName());
//        replaceMap.put(Placeholders.TEAM_MEMBER_SKULL_TEXTURE, null); // TODO: get skull texture owner
//
//
//        buttonItem.setName(PlaceholdersUtils.replace(buttonItem.getName(), replaceMap));
//        buttonItem.setLore(PlaceholdersUtils.replaceList(buttonItem.getLore(), replaceMap));
//        buttonItem.setSkullTexture(PlaceholdersUtils.replace(buttonItem.getSkullTexture(), replaceMap));
    }
}

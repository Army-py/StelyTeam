package fr.army.stelyteam.menu.button.impl;

import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.PlaceholdersUtils;
import fr.army.stelyteam.config.message.Placeholders;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.ButtonItem;
import fr.army.stelyteam.menu.button.ComponentButton;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.AbstractMenuView;
import fr.army.stelyteam.menu.view.impl.PagedMenuView;
import fr.army.stelyteam.team.Team;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TeamAllianceComponentButton<C> extends ComponentButton<PagedMenuView<C>, C> {

    public TeamAllianceComponentButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {

    }

    @Override
    public @NotNull Button<? extends AbstractMenuView<?>> get(@NotNull ButtonTemplate buttonTemplate) {
        return new TeamAllianceComponentButton<>(buttonTemplate);
    }

    @Override
    public void replacePlaceholders(C component){
        if (!(component instanceof Team team)) {
            return;
        }

        final ButtonItem buttonItem = buttonTemplate.getButtonItem();

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
        replaceMap.put(Placeholders.TEAM_OWNER_SKULL_TEXTURE, null); // TODO: get skull texture owner


        buttonItem.setName(PlaceholdersUtils.replace(buttonItem.getName(), replaceMap));
        buttonItem.setLore(PlaceholdersUtils.replaceList(buttonItem.getLore(), replaceMap));
        buttonItem.setSkullTexture(PlaceholdersUtils.replace(buttonItem.getSkullTexture(), replaceMap));

    }
}

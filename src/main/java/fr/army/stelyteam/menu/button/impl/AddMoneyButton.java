package fr.army.stelyteam.menu.button.impl;

import fr.army.stelyteam.menu.view.AbstractMenuView;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversation.ConvAddMoney;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.impl.TeamMenuView;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import org.jetbrains.annotations.NotNull;

public class AddMoneyButton extends Button<TeamMenuView> {

    private final StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    private final ConversationBuilder conv = plugin.getConversationBuilder();

    public AddMoneyButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        final Player player = (Player) clickEvent.getWhoClicked();

        player.closeInventory();
        conv.getNameInput(player, new ConvAddMoney(plugin));
    }

    @Override
    public @NotNull Button<TeamMenuView> get(@NotNull ButtonTemplate buttonTemplate) {
        return new AddMoneyButton(buttonTemplate);
    }
}

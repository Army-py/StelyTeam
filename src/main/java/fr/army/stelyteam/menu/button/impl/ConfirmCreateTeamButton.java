package fr.army.stelyteam.menu.button.impl;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.conversation.ConvGetTeamName;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.MenuView;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;

public class ConfirmCreateTeamButton extends Button<MenuView> {

    private final StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    private final ConversationBuilder conv = plugin.getConversationBuilder();

    public ConfirmCreateTeamButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        final Player player = (Player) clickEvent.getWhoClicked();

        player.closeInventory();
        conv.getNameInput(player, new ConvGetTeamName(plugin));

        openPreviousMenu(Optional.empty());
    }
}

package fr.army.stelyteam.menu.button.impl;

import java.util.Optional;

import fr.army.stelyteam.menu.view.AbstractMenuView;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.conversation.ConvGetTeamName;
import fr.army.stelyteam.menu.Menus;
import fr.army.stelyteam.menu.button.Button;
import fr.army.stelyteam.menu.button.template.ButtonTemplate;
import fr.army.stelyteam.menu.view.impl.MenuView;
import fr.army.stelyteam.utils.builder.conversation.ConversationBuilder;
import org.jetbrains.annotations.NotNull;

public class CreateTeamButton extends Button<MenuView> {

    private final StelyTeamPlugin plugin = StelyTeamPlugin.getPlugin();
    private final ConversationBuilder conv = plugin.getConversationBuilder();

    public CreateTeamButton(ButtonTemplate buttonTemplate) {
        super(buttonTemplate);
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        final Player player = (Player) clickEvent.getWhoClicked();

        final double teamCreationPrice = Config.priceCreateTeam;
        if (plugin.getEconomyManager().hasEnough(player, teamCreationPrice)){
            if (Config.confirmCreateTeam)
                Menus.MENU_CONFIRM_CREATE_TEAM.createView(player, Optional.empty()).open();
            else{
                player.closeInventory();
                conv.getNameInput(player, new ConvGetTeamName(plugin));
            }
        }else{
            player.sendMessage(Messages.PREFIX.getMessage() + Messages.NOT_ENOUGH_MONEY.getMessage());
        }
    }

    @Override
    public @NotNull Button<MenuView> get(@NotNull ButtonTemplate buttonTemplate) {
        return new CreateTeamButton(buttonTemplate);
    }
}

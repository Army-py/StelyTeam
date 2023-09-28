package fr.army.stelyteam.conversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.builder.ColorsBuilder;
import fr.army.stelyteam.utils.manager.EconomyManager;


public class ConvGetTeamPrefix extends StringPrompt {

    private final EconomyManager economyManager;
    private final ColorsBuilder colorBuilder;
    private final Team team;


    public ConvGetTeamPrefix(StelyTeamPlugin plugin, Team team) {
        this.economyManager = plugin.getEconomyManager();
        this.colorBuilder = plugin.getColorsBuilder();
        this.team = team;
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        
        if (colorBuilder.prefixTeamIsTooLong(answer)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_PREFIX_TOO_LONG.getMessage());
            return this;
        }else if (colorBuilder.containsBlockedColors(answer)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_PREFIX_HAS_BLOCKED_COLORS.getMessage());
            return this;
        }


        economyManager.removeMoneyPlayer(author, Config.priceCreateTeam);
        con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_CREATED.getMessage());

        
        //TODO: Modifier la méthode
        if (Config.openMenuWhenCreated) StelyTeamPlugin.getPlugin().openMainInventory(author, team);

        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return Messages.PREFIX.getMessage() + Messages.SEND_TEAM_PREFIX.getMessage();
    }
}
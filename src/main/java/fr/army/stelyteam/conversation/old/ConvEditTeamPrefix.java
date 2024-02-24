package fr.army.stelyteam.conversation.old;

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


public class ConvEditTeamPrefix extends StringPrompt {

    private EconomyManager economyManager;
    private ColorsBuilder colorBuilder;


    public ConvEditTeamPrefix(StelyTeamPlugin plugin) {
        this.economyManager = plugin.getEconomyManager();
        this.colorBuilder = new ColorsBuilder(plugin);
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Team team = Team.initFromPlayerName(authorName);
        
        if (colorBuilder.prefixTeamIsTooLong(answer)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_PREFIX_TOO_LONG.getMessage());
            return this;
        }else if (colorBuilder.prefixTeamIsTooShort(answer)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_PREFIX_TOO_SHORT.getMessage());
            return this;
        }else if (colorBuilder.containsBlockedColors(answer)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_PREFIX_HAS_BLOCKED_COLORS.getMessage());
            return this;
        }

        economyManager.removeMoneyPlayer(author, Config.priceEditTeamPrefix);
        con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_PREFIX_EDITED.getMessage(ColorsBuilder.replaceColor(answer)));
        team.updateTeamPrefix(answer);
        team.refreshTeamMembersInventory(authorName);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return Messages.PREFIX.getMessage() + Messages.SEND_NEW_TEAM_PREFIX.getMessage();
    }
}
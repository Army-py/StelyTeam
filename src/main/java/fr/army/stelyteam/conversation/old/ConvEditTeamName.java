package fr.army.stelyteam.conversation.old;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.database.DatabaseManager;

public class ConvEditTeamName extends StringPrompt {

    private DatabaseManager sqlManager;
    private EconomyManager economyManager;


    public ConvEditTeamName(StelyTeamPlugin plugin){
        this.sqlManager = plugin.getDatabaseManager();
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        Team team = Team.initFromPlayerName(authorName);

        if (nameTeamIsTooLong(answer)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_NAME_TOO_LONG.getMessage());
            return this;
        }else if (nameIsToShoort(answer)){
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_NAME_TOO_SHORT.getMessage());
            return this;
        }else if (sqlManager.teamNameExists(answer)){
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_NAME_ALREADY_EXISTS.getMessage());
            return this;
        }else if (answer.contains(" ")){
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_NAME_CANNOT_CONTAINS_SPACE.getMessage());
            return this;
        }

        economyManager.removeMoneyPlayer(author, Config.priceEditTeamName);
        con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_NAME_EDITED.getMessage(answer));
        team.updateTeamName(answer);
        team.refreshTeamMembersInventory(authorName);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return Messages.PREFIX.getMessage() + Messages.SEND_TEAM_NAME.getMessage();
    }

    private boolean nameTeamIsTooLong(String teamName){
        return teamName.length() > Config.teamNameMaxLength;
    }

    private boolean nameIsToShoort(String teamName){
        return teamName.length() < Config.teamNameMinLength;
    }
}
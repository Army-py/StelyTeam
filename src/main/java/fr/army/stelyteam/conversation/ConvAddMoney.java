package fr.army.stelyteam.conversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.Config;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.EconomyManager;

public class ConvAddMoney extends StringPrompt {

    private EconomyManager economy;


    public ConvAddMoney(StelyTeamPlugin plugin) {
        this.economy = plugin.getEconomyManager();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        final Player author = (Player) con.getForWhom();
        final double money = Double.parseDouble(answer);
        Team team = Team.initFromPlayerName(author.getName());

        if (!economy.hasEnough(author, money)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.NOT_ENOUGH_MONEY.getMessage());
            return null;
        }else if (teamReachedMaxMoney(team, money)) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_BANK_REACHED_MAX_MONEY.getMessage());
            return null;
        }else if (money < 0) {
            con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.CANNOT_ADD_NEGATIVE_MONEY.getMessage());
            return null;
        }

        economy.removeMoneyPlayer(author, money);
        team.incrementTeamMoney(money);
        con.getForWhom().sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_BANK_MONEY_ADDED.getMessage());
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return Messages.PREFIX.getMessage() + Messages.SEND_DEPOSIT_MONEY_AMOUNT.getMessage();
    }


    private boolean teamReachedMaxMoney(Team team, double money) {
        return team.getTeamMoney() + money > Config.teamBankMaxMoneyLimit;
    }
}
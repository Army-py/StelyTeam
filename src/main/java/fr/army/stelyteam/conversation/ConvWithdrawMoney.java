package fr.army.stelyteam.conversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.config.message.Messages;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.EconomyManager;

public class ConvWithdrawMoney extends StringPrompt {

    private EconomyManager economyManager;


    public ConvWithdrawMoney(StelyTeamPlugin plugin) {
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        Team team = Team.initFromPlayerName(author.getName());
        String teamName = team.getName();
        Double teamMoney = team.getTeamMoney();
        Double money;

        if (answer.equals("all")){
            money = teamMoney;
        }else{
            try {
                money = Double.parseDouble(answer);
            } catch (NumberFormatException e) {
                author.sendRawMessage(Messages.PREFIX.getMessage() + Messages.WRONG_AMOUNT.getMessage());
                return null;
            }
        }

        if (teamReachedMinMoney(teamName, money, teamMoney)) {
            author.sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_BANK_REACHED_MIN_MONEY.getMessage(teamMoney.toString()));
            return null;
        }else if (money < 0) {
            author.sendRawMessage(Messages.PREFIX.getMessage() + Messages.CANNOT_WITHDRAW_NEGATIVE_MONEY.getMessage());
            return null;
        }

        economyManager.addMoneyPlayer(author, money);
        author.sendRawMessage(Messages.PREFIX.getMessage() + Messages.TEAM_BANK_MONEY_WITHDRAWN.getMessage(money.toString()));
        team.decrementTeamMoney(money);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return Messages.PREFIX.getMessage() + Messages.SEND_WITHDRAW_MONEY_AMOUNT.getMessage();
    }


    private boolean teamReachedMinMoney(String teamID, Double money, Double teamMoney) {
        return teamMoney - money < 0;
    }
}
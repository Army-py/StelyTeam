package fr.army.stelyteam.conversation;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.team.Team;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvAddMoney extends StringPrompt {

    private YamlConfiguration config;
    private MessageManager messageManager;
    private EconomyManager economy;


    public ConvAddMoney(StelyTeamPlugin plugin) {
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.economy = plugin.getEconomyManager();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        Team team = Team.initFromPlayerName(author.getName());
        Double money = Double.parseDouble(answer);

        if (!economy.checkMoneyPlayer(author, money)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.not_enough_money"));
            return null;
        }else if (teamReachedMaxMoney(team, money)) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_team.add_money.team_reached_max_money"));
            return null;
        }else if (money < 0) {
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_team.add_money.cant_add_negative_money"));
            return null;
        }

        economy.removeMoneyPlayer(author, money);
        con.getForWhom().sendRawMessage(messageManager.getMessage("manage_team.add_money.money_added"));
        team.incrementTeamMoney(money);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return messageManager.getMessage("manage_team.add_money.send_money_amount");
    }


    private boolean teamReachedMaxMoney(Team team, double money) {
        Double teamMoney = team.getTeamMoney();
        return teamMoney + money > config.getDouble("teamMaxMoney");
    }
}
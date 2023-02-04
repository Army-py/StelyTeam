package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.manager.EconomyManager;
import fr.army.stelyteam.utils.manager.MessageManager;
import fr.army.stelyteam.utils.manager.MySQLManager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvAddMoney extends StringPrompt {

    private MySQLManager sqlManager;
    private YamlConfiguration config;
    private MessageManager messageManager;
    private EconomyManager economy;


    public ConvAddMoney(StelyTeamPlugin plugin) {
        this.sqlManager = plugin.getSQLManager();
        this.config = plugin.getConfig();
        this.messageManager = plugin.getMessageManager();
        this.economy = plugin.getEconomyManager();
    }

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String teamID = sqlManager.getTeamNameFromPlayerName(author.getName());
        Double money = Double.parseDouble(answer);

        if (!economy.checkMoneyPlayer(author, money)) {
            // con.getForWhom().sendRawMessage("Vous n'avez pas assez d'argent");
            con.getForWhom().sendRawMessage(messageManager.getMessage("common.not_enough_money"));
            return null;
        }else if (teamReachedMaxMoney(teamID, money)) {
            // con.getForWhom().sendRawMessage("La team a déjà atteint le maximum de money");
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_team.add_money.team_reached_max_money"));
            return null;
        }else if (money < 0) {
            // con.getForWhom().sendRawMessage("Vous ne pouvez pas ajouter un montant négatif");
            con.getForWhom().sendRawMessage(messageManager.getMessage("manage_team.add_money.cant_add_negative_money"));
            return null;
        }

        economy.removeMoneyPlayer(author, money);
        // con.getForWhom().sendRawMessage("Le montant a été ajouté");
        con.getForWhom().sendRawMessage(messageManager.getMessage("manage_team.add_money.money_added"));
        sqlManager.incrementTeamMoney(teamID, money);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le montant à ajouter";
        return messageManager.getMessage("manage_team.add_money.send_money_amount");
    }


    private boolean teamReachedMaxMoney(String teamID, double money) {
        Double teamMoney = sqlManager.getTeamMoney(teamID);
        return teamMoney + money > config.getDouble("teamMaxMoney");
    }
}
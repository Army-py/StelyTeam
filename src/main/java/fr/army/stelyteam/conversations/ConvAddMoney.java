package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.MessageManager;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvAddMoney extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(author.getName());
        Double money = Double.parseDouble(answer);
        EconomyManager eco = new EconomyManager();

        if (!eco.checkMoneyPlayer(author, money)) {
            // con.getForWhom().sendRawMessage("Vous n'avez pas assez d'argent");
            con.getForWhom().sendRawMessage(MessageManager.getMessage("common.not_enough_money"));
            return null;
        }else if (teamReachedMaxMoney(teamID, money)) {
            // con.getForWhom().sendRawMessage("La team a déjà atteint le maximum de money");
            con.getForWhom().sendRawMessage(MessageManager.getMessage("manage_team.add_money.team_reached_max_money"));
            return null;
        }else if (money < 0) {
            // con.getForWhom().sendRawMessage("Vous ne pouvez pas ajouter un montant négatif");
            con.getForWhom().sendRawMessage(MessageManager.getMessage("manage_team.add_money.cant_add_negative_money"));
            return null;
        }

        eco.removeMoneyPlayer(author, money);
        // con.getForWhom().sendRawMessage("Le montant a été ajouté");
        con.getForWhom().sendRawMessage(MessageManager.getMessage("manage_team.add_money.money_added"));
        StelyTeamPlugin.sqlManager.incrementTeamMoney(teamID, money);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le montant à ajouter";
        return MessageManager.getMessage("manage_team.add_money.send_money_amount");
    }


    private boolean teamReachedMaxMoney(String teamID, double money) {
        Double teamMoney = StelyTeamPlugin.sqlManager.getTeamMoney(teamID);
        return teamMoney + money > StelyTeamPlugin.config.getDouble("teamMaxMoney");
    }
}
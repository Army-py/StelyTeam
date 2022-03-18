package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvAddMoney extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(author.getName());
        Integer money = Integer.parseInt(answer);

        if (teamReachedMaxMoney(teamID, money)) {
            con.getForWhom().sendRawMessage("La team a déjà atteint le maximum de money");
            return null;
        }else if (money < 0) {
            con.getForWhom().sendRawMessage("Vous ne pouvez pas ajouter un montant négatif");
            return null;
        }

        con.getForWhom().sendRawMessage("Le montant a été ajouté");
        StelyTeamPlugin.sqlManager.incrementTeamMoney(teamID, money);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Envoie le montant à ajouter";
    }


    private boolean teamReachedMaxMoney(String teamID, Integer money) {
        Integer teamMoney = StelyTeamPlugin.sqlManager.getTeamMoney(teamID);
        return teamMoney + money > StelyTeamPlugin.config.getInt("teamMaxMoney");
    }
}
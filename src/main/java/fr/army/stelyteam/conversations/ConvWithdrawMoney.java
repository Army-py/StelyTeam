package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvWithdrawMoney extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(author.getName());
        Integer money = Integer.parseInt(answer);

        if (teamReachedMinMoney(teamID, money)) {
            con.getForWhom().sendRawMessage("La team a déjà atteint le minimum d'argent");
            return null;
        }else if (money < 0) {
            con.getForWhom().sendRawMessage("Vous ne pouvez pas ajouter un montant négatif");
            return null;
        }

        con.getForWhom().sendRawMessage("Le montant a été retiré");
        StelyTeamPlugin.sqlManager.decrementTeamMoney(teamID, money);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Envoie le montant à retirer";
    }


    private boolean teamReachedMinMoney(String teamID, Integer money) {
        Integer teamMoney = StelyTeamPlugin.sqlManager.getTeamMoney(teamID);
        return teamMoney - money < 0;
    }
}
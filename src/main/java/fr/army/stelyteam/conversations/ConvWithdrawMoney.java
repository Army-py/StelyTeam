package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.EconomyManager;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvWithdrawMoney extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(author.getName());
        Float teamMoney = StelyTeamPlugin.sqlManager.getTeamMoney(teamID);
        EconomyManager eco = new EconomyManager();
        Float money;

        if (answer.equals("all")){
            money = teamMoney;
        }else{
            try {
                money = Float.parseFloat(answer);
            } catch (NumberFormatException e) {
                author.sendRawMessage("Veuillez entrer un nombre");
                return null;
            }
        }

        if (teamReachedMinMoney(teamID, money, teamMoney)) {
            con.getForWhom().sendRawMessage("Vous ne pouvez pas retirer plus de " + teamMoney + "€");
            return null;
        }else if (money < 0) {
            con.getForWhom().sendRawMessage("Vous ne pouvez pas ajouter un montant négatif");
            return null;
        }

        eco.addMoneyPlayer(author, money);
        con.getForWhom().sendRawMessage("Le montant a été retiré");
        StelyTeamPlugin.sqlManager.decrementTeamMoney(teamID, money);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Envoie le montant à retirer";
    }


    private boolean teamReachedMinMoney(String teamID, Float money, Float teamMoney) {
        return teamMoney - money < 0;
    }
}
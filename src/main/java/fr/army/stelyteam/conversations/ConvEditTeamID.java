package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.EconomyManager;
import fr.army.stelyteam.utils.MessageManager;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvEditTeamID extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();
        String teamID = StelyTeamPlugin.sqlManager.getTeamIDFromPlayer(authorName);

        if (nameTeamIsTooLong(answer)) {
            // con.getForWhom().sendRawMessage("Le nom est trop long");
            con.getForWhom().sendRawMessage(MessageManager.getMessage("common.name_is_too_long"));
            return this;
        }else if (StelyTeamPlugin.sqlManager.teamIdExist(answer)){
            // con.getForWhom().sendRawMessage("Ce nom de team existe déjà");
            con.getForWhom().sendRawMessage(MessageManager.getMessage("common.name_already_exists"));
            return this;
        }else if (answer.contains(" ")){
            // con.getForWhom().sendRawMessage("Le nom ne doit pas contenir d'espace");
            con.getForWhom().sendRawMessage(MessageManager.getMessage("common.name_cannot_contain_space"));
            return this;
        }

        new EconomyManager().removeMoneyPlayer(author, StelyTeamPlugin.config.getDouble("prices.editTeamId"));
        // con.getForWhom().sendRawMessage("Le nom a été changé par " + answer);
        con.getForWhom().sendRawMessage(MessageManager.getReplaceMessage("manage_team.edit_team_id.team_id_edited", answer));
        StelyTeamPlugin.sqlManager.updateTeamID(teamID, answer);
        StelyTeamPlugin.sqliteManager.updateTeamID(teamID, answer);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le nouveau nom de team";
        return MessageManager.getMessage("manage_team.edit_team_id.send_team_id");
    }


    private boolean nameTeamIsTooLong(String teamName){
        return teamName.length() > StelyTeamPlugin.config.getInt("teamNameMaxLength");
    }
}
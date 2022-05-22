package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
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
            con.getForWhom().sendRawMessage("Le nom est trop long");
            return this;
        }else if (StelyTeamPlugin.sqlManager.teamIdExist(answer)){
            con.getForWhom().sendRawMessage("Ce nom de team existe déjà");
            return this;
        }


        con.getForWhom().sendRawMessage("Le nom a été changé par " + answer);
        StelyTeamPlugin.sqlManager.updateTeamID(teamID, answer, authorName);
        StelyTeamPlugin.sqliteManager.updateTeamID(teamID, answer);
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Envoie le nouveau nom de team";
    }


    private boolean nameTeamIsTooLong(String teamName){
        return teamName.length() > StelyTeamPlugin.config.getInt("teamNameMaxLength");
    }
}
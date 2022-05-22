package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.ConversationBuilder;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ConvGetTeamId extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext con, String answer) {
        Player author = (Player) con.getForWhom();
        String authorName = author.getName();

        if (nameTeamIsTooLong(answer)) {
            con.getForWhom().sendRawMessage("Le nom est trop long");
            return this;
        }else if (StelyTeamPlugin.sqlManager.teamIdExist(answer)){
            con.getForWhom().sendRawMessage("Ce nom de team existe déjà");
            return this;
        }


        StelyTeamPlugin.addCreationTeamTempName(authorName, answer);
        new ConversationBuilder().getNameInput(author, new ConvGetTeamPrefix());
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        return "Envoie le nom de team";
    }


    private boolean nameTeamIsTooLong(String teamName){
        return teamName.length() > StelyTeamPlugin.config.getInt("teamNameMaxLength");
    }
}
package fr.army.stelyteam.conversations;

import fr.army.stelyteam.StelyTeamPlugin;
import fr.army.stelyteam.utils.MessageManager;
import fr.army.stelyteam.utils.conversation.ConversationBuilder;

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


        StelyTeamPlugin.addCreationTeamTempName(authorName, answer);
        new ConversationBuilder().getNameInput(author, new ConvGetTeamPrefix());
        return null;
    }

    @Override
    public String getPromptText(ConversationContext arg0) {
        // return "Envoie le nom de team";
        return MessageManager.getMessage("manage_team.creation.send_team_id");
    }


    private boolean nameTeamIsTooLong(String teamName){
        return teamName.length() > StelyTeamPlugin.config.getInt("teamNameMaxLength");
    }
}